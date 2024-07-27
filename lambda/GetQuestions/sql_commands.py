from typing import Optional

#### MAIN FUNCTIONS ####
# Check for a question with a date in the proper range of today (4am today and 4am next day)
get_pulled_question = """
    SELECT question_id 
    FROM `PulseDaily`.`QuestionsQueue`
    WHERE 
        org_id = %s AND
        DATE(pull_time) = CURDATE()
    ORDER BY queue_id ASC
    LIMIT 1;
"""

# Check for queue questions with NULL pull_time (aka pull the first question of the day)
get_first_question_not_pulled = """
    SELECT queue_id, question_id 
    FROM `PulseDaily`.`QuestionsQueue`
    WHERE org_id = %s AND pull_time IS NULL
    ORDER BY queue_id ASC
    LIMIT 1;
"""

#### HELPERS ####
# Get employee id and organization id
get_employee_info = """
    SELECT 
        emp_id, 
        org_id
    FROM `PulseDaily`.`Employees`
    WHERE emp_hash = '%s';
"""

# Repopulate queue with a new cycle of questions
populate_queue = """
    INSERT INTO `PulseDaily`.`QuestionsQueue` (question_id, org_id)
    SELECT question_id, %s AS org_id FROM `PulseDaily`.`Questions` WHERE is_generic = True
    UNION ALL
    SELECT question_id, org_id FROM `PulseDaily`.`OrganizationsQuestions` WHERE org_id = %s;
"""

# Get question content from question id
get_question = """
    SELECT content 
    FROM `PulseDaily`.`Questions`
    WHERE question_id = %s
    LIMIT 1;
"""

# Update queue question timestamp
update_timestamp = """
    UPDATE `PulseDaily`.`QuestionsQueue`
    SET pull_time = NOW()
    WHERE queue_id = %s;
"""
