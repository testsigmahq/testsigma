ALTER TABLE `test_devices` DROP FOREIGN KEY `fk_app_upload_id_in_test_devices_to_uploads`;
ALTER TABLE `test_devices` DROP INDEX `index_test_devices_on_app_upload_id`;
