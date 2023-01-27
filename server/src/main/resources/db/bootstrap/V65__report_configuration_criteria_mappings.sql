DROP TABLE IF EXISTS `report_configuration_criteria_mappings`;
CREATE TABLE `report_configuration_criteria_mappings`
(
    `id`           BIGINT(20) NOT NULL AUTO_INCREMENT,
    `criteria_id`     BIGINT(20) NOT NULL,
    `configuration_id` BIGINT(20) NOT NULL,
    `created_date` DATETIME   DEFAULT CURRENT_TIMESTAMP,
    `updated_date` DATETIME   DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_critera_id_to_report_criteria` FOREIGN KEY (`criteria_id`) REFERENCES `report_criteria` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
    CONSTRAINT `fk_configuration_id_to_report_configuration` FOREIGN KEY (`configuration_id`) REFERENCES `report_configurations` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
