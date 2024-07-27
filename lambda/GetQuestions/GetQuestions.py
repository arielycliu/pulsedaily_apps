from package import pymysql 
from package.pymysql.cursors import DictCursor
import logging
import json
import sys
import os
import base64
from sql_commands import (
    get_pulled_question,
    get_first_question_not_pulled,
    get_employee_info,
    populate_queue,
    get_question,
    update_timestamp
) 

# Configure logging
logger = logging.getLogger()
logger.setLevel(logging.INFO)
handler = logging.StreamHandler(sys.stdout)
formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
handler.setFormatter(formatter)
logger.addHandler(handler)

# Get environmental variables
try:
    user_name = os.environ['USER_NAME']
    password = os.environ['PASSWORD']
    rds_proxy_host = os.environ['RDS_PROXY_HOST']
    db_name = os.environ['DB_NAME']
except KeyError as e:
    logger.error("ERROR: Environment variable %s not set", e)
    sys.exit(1)


def return_response(cursor, question_id):
    
    # return response with question_id, question content
    try:
        # get the question content from the questions table
        cursor.execute(get_question, (question_id,))
        result = cursor.fetchone()
        if not result:
            raise ValueError("Question not found for question_id: %s" % question_id)
        content = result["content"]

        return {
            "status": 200,
            "body": json.dumps(
                {
                    "question_id": question_id,
                    "content": content
                }
            )
        }
    except ValueError as e:
        logger.error("ERROR: %s", e)
        return {"status": 404, "body": str(e)}

def lambda_handler(event, context):
    try:
        # Parse body to get employee email
        body_str = event["body"]
        if not body_str:
            raise ValueError("Request body is missing")
        
        body = json.loads(body_str)
        emp_hash = body.get("emp_hash")
        if not emp_hash:
            raise ValueError("Employee hash is missing in the request body")

        connection = pymysql.connect(
            host=rds_proxy_host, 
            user=user_name, 
            passwd=password, 
            db=db_name, 
            connect_timeout=3,
            cursorclass=DictCursor
        )
        logger.info("SUCCESS: Connection to RDS for MySQL instance succeeded")

        with connection.cursor() as cursor:

            # Get employee id and org id using employee email
            employee_search = f"SELECT * FROM Employees WHERE emp_hash = '{emp_hash}' LIMIT 1;"
            cursor.execute(employee_search)
            result = cursor.fetchall()
 
            if not result:
                raise ValueError("Employee hash not found in database " + emp_hash)
            org_id = result[0]["org_id"]  # used in further queries

            # Assume that someone has already pulled the question today, pull question with timestamp for today
            cursor.execute(get_pulled_question, (org_id))
            result = cursor.fetchone()
            if result:
                logger.info("Pulling question of the day...")
                question_id = result["question_id"]
                return return_response(cursor, question_id)

            # Otherwise assume we are the first to pull a question today
            # Pull the first question with a null timestamp
            cursor.execute(get_first_question_not_pulled, (org_id))
            result = cursor.fetchone()
            if result:
                logger.info("Pulling a new question of the day...")
                # update question timestamp
                queue_id = result["queue_id"]
                question_id = result["question_id"]
                logger.info(f"Updating timestamp for {queue_id = }")
                cursor.execute(update_timestamp, (queue_id))
                connection.commit()
                return return_response(cursor, question_id)

            # Since there are no questions left that haven't been pulled, we need to refill the queue
            logger.info("Repopulating queue with new questions...")
            cursor.execute(populate_queue, (org_id, org_id))
            connection.commit()  # save changes to the table

            # Repeat to get the first question with a null timestamp
            cursor.execute(get_first_question_not_pulled, (org_id))
            result = cursor.fetchone()
            if result:
                logger.info("Pulling a new question of the day...")
                # update question timestamp
                queue_id = result["queue_id"]
                question_id = result["question_id"]
                logger.info(f"Updating timestamp for {queue_id = }")
                cursor.execute(update_timestamp, (queue_id))
                connection.commit()
                return return_response(cursor, question_id)

            logger.error("ERROR: No questions available after repopulating queue.")
            return {"status": 404, "body": "No questions available"}

    except ValueError as e:
        logger.error("ERROR: %s", e)
        return {"status": 400, "body": str(e)}
    except pymysql.MySQLError as e:
        logger.error("ERROR: MySQL error occurred.")
        logger.error(e)
        return {"status": 500, "body": "Internal server error"}
    except Exception as e:
        logger.error("ERROR: An unexpected error occurred.")
        logger.error(e)
        return {"status": 500, "body": "Internal server error"}