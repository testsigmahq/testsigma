ALTER TABLE `agents` ADD COLUMN `imported_id`  BIGINT(20) DEFAULT NULL;
ALTER TABLE `attachments` ADD COLUMN `imported_id`  BIGINT(20) DEFAULT NULL;
ALTER TABLE `elements` ADD COLUMN `imported_id`  BIGINT(20) DEFAULT NULL;
ALTER TABLE `element_screen_names` ADD COLUMN `imported_id`  BIGINT(20) DEFAULT NULL;
ALTER TABLE `natural_text_actions` ADD COLUMN `import_to_web`  BIGINT(20) DEFAULT NULL;
ALTER TABLE `natural_text_actions` ADD COLUMN `import_to_mobile_web`  BIGINT(20) DEFAULT NULL;
ALTER TABLE `natural_text_actions` ADD COLUMN `import_to_android_native`  BIGINT(20) DEFAULT NULL;
ALTER TABLE `natural_text_actions` ADD COLUMN `import_to_ios_native`  BIGINT(20) DEFAULT NULL;
ALTER TABLE `rest_step_details` ADD COLUMN `imported_id`  BIGINT(20) DEFAULT NULL;
ALTER TABLE `test_cases` ADD COLUMN `imported_id`  BIGINT(20) DEFAULT NULL;
ALTER TABLE `test_case_priorities` ADD COLUMN `imported_id`  BIGINT(20) DEFAULT NULL;
ALTER TABLE `test_case_types` ADD COLUMN `imported_id`  BIGINT(20) DEFAULT NULL;
ALTER TABLE `test_data` ADD COLUMN `imported_id`  BIGINT(20) DEFAULT NULL;
ALTER TABLE `test_devices` ADD COLUMN `imported_id`  BIGINT(20) DEFAULT NULL;
ALTER TABLE `test_steps` ADD COLUMN `imported_id`  BIGINT(20) DEFAULT NULL;
ALTER TABLE `uploads` ADD COLUMN `imported_id`  BIGINT(20) DEFAULT NULL;
ALTER TABLE `upload_versions` ADD COLUMN `imported_id`  BIGINT(20) DEFAULT NULL;
ALTER TABLE `test_plans` ADD COLUMN `imported_id`  BIGINT(20) DEFAULT NULL;
ALTER TABLE `test_suites` ADD COLUMN `imported_id`  BIGINT(20) DEFAULT NULL;