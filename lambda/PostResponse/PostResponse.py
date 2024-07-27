from package import pymysql
from package.pymysql.cursors import DictCursor
import logging
import json
import sys
import os
from sql_commands import (
    get_employee_info,
    create_table,
    insert_response
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

def lambda_handler(event, context):
    try:
        # Parse body to get employee hash, question id, response rating and response details
        body_str = event["body"]
        if not body_str:
            raise ValueError("Request body is missing")
        
        body = json.loads(body_str)
        emp_hash = body.get("emp_hash")
        question_id = body.get("question_id")
        rating = body.get("rating")
        details = body.get("details")

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
            cursor.execute(get_employee_info, (emp_hash))
            result = cursor.fetchone()
            if not result:
                return {
                    "status": 404,
                    "body": json.dumps(f"Employee not found: {emp_hash}")
                }
            emp_id = result["emp_id"]  # used in response table

            cursor.execute(create_table)  # create table if missing

            # Insert response into table
            cursor.execute(insert_response, (question_id, rating, details, emp_id))

        connection.commit()
        logger.info("Successfully added record to Responses table.")
        
        return {
            "statusCode": 200,
            "body": json.dumps("Success")
        }
    except ValueError as e:
        logger.error("ERROR: %s", e)
        return {"statusCode": 400, "body": str(e)}
    except pymysql.MySQLError as e:
        logger.error("ERROR: MySQL error occurred.")
        logger.error(e)
        return {"statusCode": 500, "body": "Internal server error"}
    except Exception as e:
        logger.error("ERROR: An unexpected error occurred.")
        logger.error(e)
        return {"statusCode": 500, "body": "Internal server error"}