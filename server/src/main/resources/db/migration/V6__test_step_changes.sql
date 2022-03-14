ALTER TABLE test_steps ADD `ignore_step_result` tinyint(1) DEFAULT '0';
UPDATE test_steps set ignore_step_result = 1 WHERE condition_type is not null;