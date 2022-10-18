DROP TABLE IF EXISTS `test_data`;
CREATE TABLE `test_data`
(
  `id`             BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `version_id`     BIGINT(20) DEFAULT NULL,
  `test_data`      MEDIUMTEXT,
  `test_data_name` VARCHAR(250) NOT NULL,
  `copied_from`    BIGINT(20) default null,
  `passwords`      JSON,
  `imported_id`    BIGINT(20) DEFAULT NULL,
  `is_migrated`    tinyint(1) DEFAULT 0,
  `created_date`   DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_date`   DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_test_data_on_version_id_and_test_data_name` (`version_id`, `test_data_name`),
  KEY `index_test_data_on_version_id` (`version_id`),
  CONSTRAINT fk_version_id_in_test_data_to_workspace_versions FOREIGN KEY (`version_id`) REFERENCES `workspace_versions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  AUTO_INCREMENT = 4
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
