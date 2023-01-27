DROP TABLE IF EXISTS `report_criteria`;
CREATE TABLE `report_criteria`
(
    `id`                        BIGINT(20) NOT NULL AUTO_INCREMENT,
    `criteria_value`            VARCHAR(255)     DEFAULT NULL,
    `criteria_condition`        VARCHAR(255)     DEFAULT NULL,
    `criteria_field`        BIGINT(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_criteria_field_to_field` FOREIGN KEY (`criteria_field`) REFERENCES `field_configurations` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
