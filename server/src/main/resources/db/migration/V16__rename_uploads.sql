ALTER TABLE `provisioning_profile_uploads` DROP FOREIGN KEY fk_upload_id_in_provisioning_profile_uploads_to_uploads;
ALTER TABLE `uploads` RENAME `upload_versions`;
ALTER TABLE `provisioning_profile_uploads` CHANGE COLUMN upload_id upload_version_id bigint(20) DEFAULT NULL;
ALTER TABLE `provisioning_profile_uploads` ADD FOREIGN KEY (upload_version_id) REFERENCES upload_versions(id);

ALTER TABLE `provisioning_profile_uploads` RENAME `provisioning_profile_upload_versions`;


ALTER TABLE `upload_versions` ADD COLUMN `upload_id` BIGINT(20) DEFAULT NULL;
ALTER TABLE `upload_versions` ADD COLUMN `last_uploaded_time` DATETIME DEFAULT NULL;
ALTER TABLE `upload_versions` MODIFY `upload_status` VARCHAR(100);


UPDATE `upload_versions` SET upload_id:=id;

Update upload_versions SET `type` = 
CASE `type`
 WHEN 1 THEN 'APK'
 WHEN 2 THEN 'IPA'
 WHEN 3 THEN 'Attachment'
END ;


CREATE TABLE `uploads` 
(
  `id`             BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `name`           VARCHAR(256) DEFAULT NULL,
  `workspace_id`   BIGINT(20)   DEFAULT NULL,
  `latest_version_id` BIGINT(20) DEFAULT NULL,
  `created_date`   DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `updated_date`   DATETIME     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_uploads_on_workspace_id_and_name` (`workspace_id`, `name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO `uploads`(`id`, `name`, `workspace_id`, `latest_version_id`, `created_date`, `updated_date`)
    SELECT id, name, workspace_id, id,  created_date, updated_date from upload_versions;


ALTER TABLE `upload_versions` ADD FOREIGN KEY (upload_id) REFERENCES uploads(id) ON DELETE CASCADE;


ALTER TABLE `test_devices` ADD COLUMN app_upload_version_id BIGINT(20) DEFAULT NULL;

ALTER TABLE `test_device_results` ADD COLUMN app_upload_version_id BIGINT(20) DEFAULT NULL;

ALTER TABLE `mobile_inspections` CHANGE COLUMN app_upload_id upload_version_id BIGINT(20) DEFAULT NULL;