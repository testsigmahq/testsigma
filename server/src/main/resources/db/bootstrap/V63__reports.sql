DROP TABLE IF EXISTS `reports`;
CREATE TABLE `reports`
(
    `id`                      BIGINT(20) NOT NULL AUTO_INCREMENT,
    `name`          VARCHAR(255)     DEFAULT NULL,
    `description`          VARCHAR(255)     DEFAULT NULL,
    `type`          VARCHAR(255)     DEFAULT NULL,
    `module_id`               BIGINT(20)       DEFAULT NULL,
    `config_id`             BIGINT(20)       DEFAULT NULL,
    `version_id`                BIGINT(20)       DEFAULT NULL,
   PRIMARY KEY (`id`),
    CONSTRAINT `fk_module_id_to_reports_module` FOREIGN KEY (`module_id`) REFERENCES `report_modules` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
    CONSTRAINT `fk_config_id_to_configurations` FOREIGN KEY (`config_id`) REFERENCES `report_configurations` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
    CONSTRAINT `fk_version_id_to_versions` FOREIGN KEY (`version_id`) REFERENCES `workspace_versions` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
