DROP TABLE IF EXISTS `default_data_generators`;
CREATE TABLE `default_data_generators`
(
  `id`            BIGINT(20) NOT NULL AUTO_INCREMENT,
  `file_id`       BIGINT(20),
  `function_name` VARCHAR(1000),
  `display_name`  VARCHAR(1000),
  `description`   TEXT,
  `arguments`     JSON,
  `created_date`  DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_date`  DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_file_id_to_default_data_generator_files` FOREIGN KEY (`file_id`) REFERENCES `default_data_generator_files` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
