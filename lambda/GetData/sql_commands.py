
get_question_via_date = """
SELECT 
    questions.question_id,
    questions.content,
    COUNT(DISTINCT responses.question_id) AS unique_questions
FROM `PulseDaily`.`Responses` AS responses
LEFT JOIN `PulseDaily`.`Employees` AS employees
    ON responses.emp_id = employees.emp_id
LEFT JOIN `PulseDaily`.`Questions` AS questions
    ON responses.question_id = questions.question_id
WHERE employees.org_id = %s
    AND DATE(responses.timestamp) = %s;
"""
get_question_via_id = """
SELECT 
    questions.question_id,
    questions.content
FROM `PulseDaily`.`Responses` AS responses
LEFT JOIN `PulseDaily`.`Employees` AS employees
    ON responses.emp_id = employees.emp_id
LEFT JOIN `PulseDaily`.`Questions` AS questions
    ON responses.question_id = questions.question_id
WHERE employees.org_id = %s
    AND responses.question_id = %s
LIMIT 1;
"""

# /data/question/{date}/{org_id}
# Pie chart format breakdown for ratings
question_ratings = """
    SELECT 
        COUNT(rating) AS rating_count,
        rating
    FROM `PulseDaily`.`Responses` AS responses
    LEFT JOIN `PulseDaily`.`Employees` AS employees
        ON responses.emp_id = employees.emp_id
    WHERE employees.org_id = %s
        AND DATE(responses.timestamp) = %s
    GROUP BY rating;
"""
# Average rating for that day
question_average_rating = """
    SELECT CAST(AVG(responses.rating) AS DOUBLE) AS rating_avg
    FROM `PulseDaily`.`Responses` AS responses
    LEFT JOIN `PulseDaily`.`Employees` AS employees
        ON responses.emp_id = employees.emp_id
    WHERE employees.org_id = %s
        AND DATE(responses.timestamp) = %s;
"""
# Response rate for that day
question_response_rate = """
    WITH EmployeeCount AS (
        SELECT 
            COUNT(emp_id) AS instance_count 
        FROM `PulseDaily`.`Employees`
        WHERE org_id = %s
    )
    SELECT 
        COUNT(responses.response_id) AS response_count,
        ec.instance_count,
        CAST((COUNT(responses.response_id) * 1.0 / ec.instance_count) AS DOUBLE) AS response_rate
    FROM `PulseDaily`.`Responses` AS responses
    LEFT JOIN `PulseDaily`.`Employees` AS employees
        ON responses.emp_id = employees.emp_id
    CROSS JOIN EmployeeCount AS ec
    WHERE employees.org_id = %s
        AND DATE(responses.timestamp) = %s;
"""
# Details for that day
question_details = """
    SELECT 
        responses.rating AS rating,
        responses.details AS details
    FROM `PulseDaily`.`Responses` AS responses
    LEFT JOIN `PulseDaily`.`Employees` AS employees
        ON responses.emp_id = employees.emp_id
    WHERE employees.org_id = %s
        AND DATE(responses.timestamp) = %s
        AND details <> ''
    ORDER BY rating ASC;
"""

# /data/questions/{question_id}/{org_id}
# Bar chat breakdown for each day for question id x
questions_ratings = """
SELECT 
	COUNT(rating) AS rating_count,
	rating,
    DATE_FORMAT(DATE(timestamp), '%%Y-%%m-%%d') AS day
FROM `PulseDaily`.`Responses` AS responses
LEFT JOIN `PulseDaily`.`Employees` AS employees
	ON responses.emp_id = employees.emp_id
WHERE employees.org_id = %s
	AND responses.question_id = %s
GROUP BY rating, day;
"""

# Average ratings for each day for question id x
questions_averages = """
SELECT 
	CAST(AVG(rating) AS DOUBLE) AS rating_avg,
    DATE_FORMAT(DATE(timestamp), '%%Y-%%m-%%d') AS day
FROM `PulseDaily`.`Responses` AS responses
LEFT JOIN `PulseDaily`.`Employees` AS employees
	ON responses.emp_id = employees.emp_id
WHERE employees.org_id = %s
	AND responses.question_id = %s
GROUP BY day;
"""

# Average ratings overall for question id x
questions_avg_overall = """
SELECT 
	CAST(AVG(rating) AS DOUBLE) AS rating_avg
FROM `PulseDaily`.`Responses` AS responses
LEFT JOIN `PulseDaily`.`Employees` AS employees
	ON responses.emp_id = employees.emp_id
WHERE employees.org_id = %s
	AND responses.question_id = %s;
"""

# Average response rate per question id
questions_response_rate_overall = """
WITH EmployeeCount AS (
    SELECT 
        COUNT(emp_id) AS instance_count 
    FROM `PulseDaily`.`Employees`
    WHERE org_id = %s
),
ResponseCount AS (
    SELECT 
        COUNT(response_id) AS response_count
    FROM `PulseDaily`.`Responses` AS responses
    LEFT JOIN `PulseDaily`.`Employees` AS employees
        ON responses.emp_id = employees.emp_id
    WHERE employees.org_id = %s
        AND responses.question_id = %s
)
SELECT 
    rc.response_count,
    ec.instance_count,
    CAST((rc.response_count * 1.0 / ec.instance_count) AS DOUBLE) AS response_rate
FROM ResponseCount AS rc
CROSS JOIN EmployeeCount AS ec;
"""

# Details (on pause)

# /data/response_rate/{org_id}
# Average response rate for all questions
response_rate_overall = """
WITH EmployeeCount AS (
    SELECT 
        COUNT(emp_id) AS instance_count 
    FROM `PulseDaily`.`Employees`
    WHERE org_id = %s
),
ResponseCount AS (
    SELECT 
        COUNT(response_id) AS response_count
    FROM `PulseDaily`.`Responses` AS responses
    LEFT JOIN `PulseDaily`.`Employees` AS employees
        ON responses.emp_id = employees.emp_id
    WHERE employees.org_id = %s
)
SELECT 
    rc.response_count,
    ec.instance_count,
    CAST((rc.response_count * 1.0 / ec.instance_count) AS DOUBLE) AS response_rate
FROM ResponseCount AS rc
CROSS JOIN EmployeeCount AS ec;
"""


# Line chart average response rate for all questions over time
response_rate_overtime = """
WITH EmployeeCount AS (
    SELECT 
        COUNT(emp_id) AS instance_count 
    FROM `PulseDaily`.`Employees`
    WHERE org_id = %s
),
ResponseCount AS (
    SELECT 
        COUNT(response_id) AS response_count,
        DATE(responses.timestamp) AS day
    FROM `PulseDaily`.`Responses` AS responses
    LEFT JOIN `PulseDaily`.`Employees` AS employees
        ON responses.emp_id = employees.emp_id
    WHERE employees.org_id = %s
    GROUP BY DATE(responses.timestamp)
)
SELECT 
    rc.response_count,
    ec.instance_count,
    CAST((rc.response_count * 1.0 / ec.instance_count) AS DOUBLE) AS response_rate,
    DATE_FORMAT(rc.day, '%%Y-%%m-%%d') AS day
FROM ResponseCount AS rc
CROSS JOIN EmployeeCount AS ec;
"""