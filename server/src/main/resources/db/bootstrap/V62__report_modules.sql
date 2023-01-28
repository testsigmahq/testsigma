DROP TABLE IF EXISTS `report_modules`;
CREATE TABLE `report_modules`
(
    `id`                      BIGINT(20) NOT NULL AUTO_INCREMENT,
    `module_name`          VARCHAR(255)     DEFAULT NULL,
    `builder_class`          VARCHAR(255)     DEFAULT NULL,
    `service_class`          VARCHAR(255)     DEFAULT NULL,
    `model_class`          VARCHAR(255)     DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
