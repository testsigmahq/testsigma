ALTER TABLE `test_plans` DROP COLUMN `is_visual_testing_enabled`;
ALTER TABLE `test_steps` ADD COLUMN `visual_enabled` tinyint(1) DEFAULT '0';
ALTER TABLE `test_step_results` ADD COLUMN `visual_enabled` tinyint(1) DEFAULT '0';