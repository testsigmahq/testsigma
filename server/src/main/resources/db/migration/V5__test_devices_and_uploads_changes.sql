LOCK TABLES `uploads` WRITE;
/*!40000 ALTER TABLE `uploads` DISABLE KEYS */;
INSERT INTO `uploads` (`id`, `name`, `path`, `file_name`, `type`,`upload_status`, `version`, `file_size`, `workspace_id`, `created_date`, `updated_date`) VALUES (6,'WordPress1','sample.com/uploads/10/6/WordPressSample.ipa',NULL,1,2,'3.1',33375441,22,'2020-04-30 11:31:45',NULL);
INSERT INTO `uploads` (`id`, `name`, `path`, `file_name`, `type`,`upload_status`, `version`, `file_size`, `workspace_id`, `created_date`, `updated_date`) VALUES (7,'WordPress2','sample.com/uploads/10/6/WordPressSample.ipa',NULL,1,2,'3.1',33375441,23,'2020-04-30 11:31:45',NULL);
INSERT INTO `uploads` (`id`, `name`, `path`, `file_name`, `type`,`upload_status`, `version`, `file_size`, `workspace_id`, `created_date`, `updated_date`) VALUES (8,'WordPress3','sample.com/uploads/10/6/WordPressSample.ipa',NULL,1,2,'3.1',33375441,24,'2020-04-30 11:31:45',NULL);
INSERT INTO `uploads` (`id`, `name`, `path`, `file_name`, `type`,`upload_status`, `version`, `file_size`, `workspace_id`, `created_date`, `updated_date`) VALUES (14,'WordPress APK','sample.com/uploads/10/14/WordPress_v12.5_apkpure.com.apk',NULL,2,1,'1.1',28882849,23,'2020-04-30 10:13:59',NULL);
INSERT INTO `uploads` (`id`, `name`, `path`, `file_name`, `type`,`upload_status`, `version`, `file_size`, `workspace_id`, `created_date`, `updated_date`) VALUES (15,'Wordpress','sample.com/uploads/13/15/WordPress_12.5.apk',NULL,1,1,'12.5',28882849,30,'2020-04-30 10:13:59',NULL);
INSERT INTO `uploads` (`id`, `name`, `path`, `file_name`, `type`,`upload_status`, `version`, `file_size`, `workspace_id`, `created_date`, `updated_date`) VALUES (16,'Wordpress','sample.com/uploads/14/16/WordPress_8.3.ipa',NULL,1,2,'8.3',33375441,31,'2020-04-30 11:31:45',NULL);
/*!40000 ALTER TABLE `uploads` ENABLE KEYS */;
UNLOCK TABLES;

ALTER TABLE `test_devices` MODIFY column `app_upload_id` BIGINT(20);
ALTER TABLE `test_devices` ADD KEY `index_test_devices_on_app_upload_id` (`app_upload_id`);
UPDATE `test_devices` SET app_upload_id = 15 WHERE app_upload_id =17 OR app_upload_id =18 OR app_upload_id =12;
UPDATE `test_devices` SET app_upload_id = NULL WHERE app_path_type='USE_PATH';
ALTER TABLE `test_devices` ADD CONSTRAINT `fk_app_upload_id_in_test_devices_to_uploads` FOREIGN
    KEY (`app_upload_id`) REFERENCES `uploads` (`id`) ON DELETE RESTRICT ON UPDATE NO ACTION;