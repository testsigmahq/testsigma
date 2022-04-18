ALTER TABLE `test_step_screenshots` ADD COLUMN `entity_type` VARCHAR(30) DEFAULT NULL;
UPDATE test_step_screenshots SET entity_type = 'EXECUTION';
