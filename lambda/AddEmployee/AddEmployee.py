from package import pymysql
from package.pymysql.cursors import DictCursor
import logging
import json
import sys
import os
from sql_commands import (
    add_employee
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
    # Parse body to get employee email
    body_str = event.get("body", "")
    if not body_str:
        raise ValueError("Request body is missing")
    
    body = json.loads(body_str)
    emp_email = body.get("email")
    email_suffix = emp_email.split('@')[1]

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
        
        # Check if duplicate
        duplicate_check = f"SELECT 1 FROM PulseDaily.Employees WHERE email = '{emp_email}';"
        cursor.execute(duplicate_check)
        result = cursor.fetchone()
        if result:
            return {
                "status": 409,
                "body": json.dumps("Employee email already in database.") 
            }

        # Based on email, figure out org_id
        find_org_id = f"SELECT org_id FROM Organizations WHERE email LIKE '%{email_suffix}' LIMIT 1;"
        cursor.execute(find_org_id)
        result = cursor.fetchone()
        if not result:
            return {
                "status": 404,
                "body": json.dumps("Organization not found.") 
            }
        org_id = result["org_id"]
        
        # Add employee to the database 
        cursor.execute(add_employee, (org_id, emp_email, org_id, emp_email))

        get_hash = f"SELECT emp_hash FROM PulseDaily.Employees WHERE org_id = {org_id} AND email = '{emp_email}';"
        cursor.execute(get_hash)
        result = cursor.fetchone()
        if not result:
            return {
                "status": 404,
                "body": json.dumps("emp hash not found.") 
            }
        emp_hash = result["emp_hash"]
    connection.commit()
    
    return {
        "status": 200,
        "emp_hash": json.dumps(emp_hash)
    }