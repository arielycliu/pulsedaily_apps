get_employee_id = """
SELECT emp_id
FROM Employees
WHERE emp_hash = %s;
"""

check_has_answered = """
SELECT response_id
FROM Responses
WHERE emp_id = %s
  AND question_id = %s
  AND DATE(timestamp) = CURDATE();
"""