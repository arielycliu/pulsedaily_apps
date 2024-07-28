from package import pymysql
from package.pymysql.cursors import DictCursor
import logging
import json
import sys
import os
from sql_commands import (
    get_employee_id,
    check_has_answered
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
        # Parse body to get employee hash & question id
        body_str = event["body"]
        if not body_str:
            raise ValueError("Request body is missing")
        
        body = json.loads(body_str)
        emp_hash = body.get("emp_hash")
        question_id = body.get("question_id")

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
            # Get employee id using employee emp_hash
            cursor.execute(get_employee_id, (emp_hash))
            result = cursor.fetchone()
            if not result:
                return {
                    "status": 404,
                    "body": "Employee not found in database."
                }
            emp_id = result["emp_id"]

            # Using emp id and timestamp, check if employee has responded to this question in the past 24 hrs
            cursor.execute(check_has_answered, (emp_id, question_id))
            result = cursor.fetchone()

            if result:
                return {
                    "status": 200,
                    "body": True
                }
            else:
                return {
                    "status": 200,
                    "body": False
                }
    except ValueError as e:
        logger.error("ERROR: %s", e)
        return {"status": 400, "body": str(e)}
    except Exception as e:
        logger.error("ERROR: An unexpected error occurred.")
        logger.error(e)
        return {"status": 500, "body": str(e)}