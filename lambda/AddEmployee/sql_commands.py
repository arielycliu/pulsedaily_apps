add_employee = """
INSERT INTO PulseDaily.Employees (org_id, email, emp_hash)
VALUES (%s, %s, SHA2(CONCAT(%s, %s), 256));
"""
