DROP TABLE IF EXISTS `default_data_generator_files`;
CREATE TABLE `default_data_generator_files`
(
  `id`            BIGINT(20) NOT NULL AUTO_INCREMENT,
  `class_package` VARCHAR(1000),
  `class_name`    VARCHAR(250),
  `display_name`  VARCHAR(1000),
  `description`   TEXT,
  `created_date`  DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_date`  DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
