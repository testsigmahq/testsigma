ALTER TABLE `test_step_results` CHANGE `field_definition_details` `element_details` JSON;

ALTER TABLE `test_steps` CHANGE `kibbutz_test_data` `addon_test_data` json NULL;
ALTER TABLE `test_steps` CHANGE `kibbutz_elements` `addon_elements` json NULL;
ALTER TABLE `test_steps` CHANGE `kibbutz_test_data_function` `addon_test_data_function` VARCHAR(250) COLLATE utf8_unicode_ci;
ALTER TABLE `test_steps` CHANGE `kibbutz_plugin_tdf_data` `addon_plugin_tdf_data` LONGTEXT NULL;

ALTER TABLE `test_devices` DROP `run_in_parallel`;

ALTER TABLE `test_step_results` CHANGE `kibbutz_test_data` `addon_test_data` json NULL;
ALTER TABLE `test_step_results` CHANGE `kibbutz_elements` `addon_elements` json NULL;
ALTER TABLE `test_step_results` CHANGE `kibbutz_action_logs` `addon_action_logs` LONGTEXT COLLATE utf8_unicode_ci;

ALTER TABLE `test_cases` ADD CONSTRAINT `index_test_cases_on_name` UNIQUE (`name`,`workspace_version_id`);

UPDATE `default_data_generators`
SET `arguments` = JSON_INSERT(
        JSON_REMOVE(arguments, '$.executionId'),
        '$.testPlanId',
        JSON_EXTRACT(arguments, '$.executionId')
    );

UPDATE `test_device_results` SET environment_settings = JSON_REMOVE(environment_settings, '$.runInParallel');
UPDATE test_steps SET condition_if = '[\"Passed\"]' WHERE condition_if ='[\"0\"]';