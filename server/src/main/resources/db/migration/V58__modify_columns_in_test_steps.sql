ALTER TABLE `test_steps` MODIFY COLUMN `test_data` json DEFAULT NULL;
ALTER TABLE `test_steps` DROP COLUMN `test_data_type`;
ALTER TABLE `test_steps` DROP COLUMN `for_loop_start_index`;
ALTER TABLE `test_steps` DROP COLUMN `for_loop_end_index`;
ALTER TABLE `test_steps` DROP COLUMN `for_loop_test_data_id`;



