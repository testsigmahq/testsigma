DROP TABLE IF EXISTS `field_configurations`;
CREATE TABLE `field_configurations`
(
    `id`                      BIGINT(20) NOT NULL AUTO_INCREMENT,
    `module_id`           BIGINT(20)       DEFAULT NULL,
    `field_name`          VARCHAR(255)     DEFAULT NULL,
    `type`          VARCHAR(255)     DEFAULT NULL,
    `order_by_allowed`                TINYINT(1)       DEFAULT '0',
    `group_by_allowed`          tinyint(1) DEFAULT '0',
    `criteria_allowed`                TINYINT(1)       DEFAULT '0',
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_module_id_to_module` FOREIGN KEY (`module_id`) REFERENCES `report_modules` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
