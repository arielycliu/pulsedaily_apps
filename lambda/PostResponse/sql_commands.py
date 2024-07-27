#### MAIN CODE ####
insert_response = """
    INSERT INTO `PulseDaily`.`Responses` (`question_id`, `rating`, `details`, `emp_id`)
    VALUES
        (%s, %s, %s, %s);
"""

#### HELPERS ####
# Get employee id and organization id
get_employee_info = """
    SELECT 
        emp_id, 
        org_id
    FROM `PulseDaily`.`Employees`
    WHERE emp_hash = %s;
"""

# Create response table
create_table = """
CREATE TABLE IF NOT EXISTS `PulseDaily`.`Responses` (
    `response_id` INT NOT NULL AUTO_INCREMENT,
    `question_id` INT NOT NULL,
    `rating` INT NOT NULL,
    `details` TEXT NULL,
    `emp_id` INT NOT NULL,
    `timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'UTC',  
    PRIMARY KEY (`resp_id`));
"""