ALTER TABLE `test_case_result_external_mappings` DROP FOREIGN KEY fk_test_case_result_id_in_external_mappings_to_test_case_results;
ALTER TABLE `test_case_result_external_mappings` DROP FOREIGN KEY fk_workspace_id_to_integrations;
ALTER TABLE `test_case_result_external_mappings` RENAME TO `entity_external_mappings`;
ALTER TABLE `entity_external_mappings` CHANGE `test_case_result_id` `entity_id` BIGINT(20) DEFAULT NULL;
ALTER TABLE `entity_external_mappings` ADD COLUMN `entity_type`varchar(255) DEFAULT NULL;
UPDATE `entity_external_mappings` SET `entity_type` = 'TEST_CASE_RESULT';
ALTER TABLE `entity_external_mappings` ADD COLUMN `push_failed` TINYINT(1) DEFAULT NULL;
ALTER TABLE `entity_external_mappings` ADD COLUMN `assets_push_failed` TINYINT(1) DEFAULT NULL;
ALTER TABLE `entity_external_mappings` ADD COLUMN `message` text DEFAULT NULL;
ALTER TABLE `entity_external_mappings` DROP COLUMN `workspace_id`;
ALTER TABLE `entity_external_mappings` ADD COLUMN `application_id` BIGINT(20) NOT NULL;
ALTER TABLE `entity_external_mappings` MODIFY COLUMN `external_id` VARCHAR(255) DEFAULT NULL;
ALTER TABLE `entity_external_mappings` ADD KEY fk_application_id (`application_id`);
ALTER TABLE `entity_external_mappings` ADD CONSTRAINT `fk_application_id_to_integrations` FOREIGN KEY (`application_id`) REFERENCES integrations (id) ON DELETE CASCADE ON UPDATE NO ACTION