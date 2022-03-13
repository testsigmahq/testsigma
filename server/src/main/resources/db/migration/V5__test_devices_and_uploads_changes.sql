DELETE FROM `test_devices` WHERE `app_upload_id` = 15 or `app_upload_id` = 16;
DELETE FROM `test_device_suites` WHERE test_device_id >314 AND test_device_id <350;

ALTER TABLE `test_devices` MODIFY column `app_upload_id` BIGINT(20);
ALTER TABLE `test_devices` ADD KEY `index_test_devices_on_app_upload_id` (`app_upload_id`);
UPDATE `test_devices` SET app_upload_id = 15 WHERE app_upload_id =17 OR app_upload_id =18 OR app_upload_id =12;
UPDATE `test_devices` SET app_upload_id = NULL WHERE app_path_type='USE_PATH';
ALTER TABLE `test_devices` ADD CONSTRAINT `fk_app_upload_id_in_test_devices_to_uploads` FOREIGN
    KEY (`app_upload_id`) REFERENCES `uploads` (`id`) ON DELETE RESTRICT ON UPDATE NO ACTION;