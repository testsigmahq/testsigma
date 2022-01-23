DROP TABLE IF EXISTS `storage_config`;
CREATE TABLE `storage_config`
(
  `id`                           BIGINT(20) NOT NULL AUTO_INCREMENT,
  `storage_type`                 VARCHAR(255) DEFAULT NULL,
  `aws_bucket_name`              VARCHAR(255) DEFAULT NULL,
  `aws_region`                   VARCHAR(255) DEFAULT NULL,
  `aws_endpoint`                 VARCHAR(255) DEFAULT NULL,
  `aws_access_key`               VARCHAR(255) DEFAULT NULL,
  `aws_secret_key`               VARCHAR(255) DEFAULT NULL,
  `azure_blob_connection_string` VARCHAR(255) DEFAULT NULL,
  `azure_blob_container_name`    VARCHAR(255) DEFAULT NULL,
  `on_premise_root_directory`    VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
