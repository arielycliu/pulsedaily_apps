from package import pymysql
from package.pymysql.cursors import DictCursor
import logging
import json
import sys
import os
import base64
from sql_commands import (
    question_ratings,
    question_average_rating,
    question_response_rate,
    question_details,
    questions_ratings,
    questions_averages,
    questions_avg_overall,
    questions_response_rate_overall,
    response_rate_overall,
    response_rate_overtime,
    get_question_via_date,
    get_question_via_id
)

# Configure logging
logger = logging.getLogger()
logger.setLevel(logging.INFO)

# Get environmental variables
try:
    user_name = os.environ['USER_NAME']
    password = os.environ['PASSWORD']
    rds_proxy_host = os.environ['RDS_PROXY_HOST']
    db_name = os.environ['DB_NAME']
except KeyError as e:
    logger.error("ERROR: Environment variable %s not set", e)
    sys.exit(1)

# Create the database connection outside of the handler to allow connections to be reused by subsequent function invocations.
try:
    connection = pymysql.connect(
        host=rds_proxy_host, 
        user=user_name, 
        passwd=password, 
        db=db_name, 
        connect_timeout=3,
        cursorclass=DictCursor
    )
    logger.info("SUCCESS: Connection to RDS for MySQL instance succeeded")
except pymysql.MySQLError as e:
    logger.error("ERROR: Could not connect to MySQL instance.")
    logger.error(e)
    sys.exit(1)

def return_data(commands):

    with connection.cursor() as cursor:
        # Get data
        result = {}
        for key, value in commands.items():
            cursor.execute(value["sql_command"], value["arguments"])
            data = cursor.fetchall()
            logger.info(data)
            result[key] = data
            
        return {
            "statusCode": 200, "body": json.dumps(result)
        }

def lambda_handler(event, context):
    # /data/question/{date}/{org_id}
    # date specific data
    if event.get('routeKey', '') == "GET /data/question/{date}/{org_id}":
        date = event.get('pathParameters', {}).get('date')
        if date is None:
            raise ValueError("Missing date in request path")
        
        org_id = event.get('pathParameters', {}).get('org_id')
        if org_id is None:
            raise ValueError("Missing org_id in request path")
        
        commands = {
            "question": {
                "sql_command": get_question_via_date,
                "arguments": (org_id, date)
            },
            "ratings": {
                "sql_command": question_ratings,
                "arguments": (org_id, date)
            },
            "rating_average": {
                "sql_command": question_average_rating,
                "arguments": (org_id, date)
            },
            "response_rate": {
                "sql_command": question_response_rate,
                "arguments": (org_id, org_id, date)
            }
            # "details": {
            #     "sql_command": question_details,
            #     "arguments": (org_id, date)
            # },
        }
        return return_data(commands)


    # /data/questions/{question_id}/{org_id}
    # question specific data
    if event.get('routeKey', '') == "GET /data/questions/{question_id}/{org_id}":
        question_id = event.get('pathParameters', {}).get('question_id')
        if question_id is None:
            raise ValueError("Missing question_id in request path")
        
        org_id = event.get('pathParameters', {}).get('org_id')
        if org_id is None:
            raise ValueError("Missing org_id in request path")
        
        commands = {
            "question": {
                "sql_command": get_question_via_id,
                "arguments": (org_id, question_id)
            },
            "ratings": {
                "sql_command": questions_ratings,
                "arguments": (org_id, question_id)
            },
            "rating_averages": {
                "sql_command": questions_averages,
                "arguments": (org_id, question_id)
            },
            "rating_average": {
                "sql_command": questions_avg_overall,
                "arguments": (org_id, question_id)
            },
            "response_rate": {
                "sql_command": questions_response_rate_overall,
                "arguments": (org_id, org_id, question_id)
            },
        }
        return return_data(commands)

    # /data/response_rate/{org_id}
    # response rate data
    if event.get('routeKey', '') == "GET /data/response_rate/{org_id}":
        org_id = event.get('pathParameters', {}).get('org_id')
        if org_id is None:
            raise ValueError("Missing org_id in request path")
        
        commands = {
            "response_rate": {
                "sql_command": response_rate_overall,
                "arguments": (org_id, org_id)
            },
            "response_rate_per_day": {
                "sql_command": response_rate_overtime,
                "arguments": (org_id, org_id)
            }
        }
        return return_data(commands)