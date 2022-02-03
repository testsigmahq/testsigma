ALTER table `backup_details` drop column is_requirement_enabled;

ALTER table `test_cases` drop FOREIGN KEY fk_requirement_id_in_test_cases_to_requirements;
ALTER table `test_cases` drop index index_on_test_cases_requirement_id;
ALTER table `test_cases` drop index index_on_test_cases_requirement_id_and_name;
ALTER table `test_cases` drop column requirement_id;
UPDATE `test_case_results` SET test_case_details = JSON_REMOVE(test_case_details, '$.requirement_id');
