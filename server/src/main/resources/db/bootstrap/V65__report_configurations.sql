DROP TABLE IF EXISTS `report_configurations`;
CREATE TABLE `report_configurations`
(
    `id`                        BIGINT(20) NOT NULL AUTO_INCREMENT,
    `selected_columns`            VARCHAR(255)     DEFAULT NULL,
    `query_string`        VARCHAR(255)     DEFAULT NULL,
    `chart_type`             VARCHAR(255)     DEFAULT NULL,
    `order_by_id`        BIGINT(20) DEFAULT NULL,
    `group_by_id`        BIGINT(20) DEFAULT NULL,
    `chart_group_field`        BIGINT(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_order_by_to_field` FOREIGN KEY (`order_by_id`) REFERENCES `field_configurations` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
    CONSTRAINT `fk_group_by_to_field` FOREIGN KEY (`group_by_id`) REFERENCES `field_configurations` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
    CONSTRAINT `fk_chart_by_to_field` FOREIGN KEY (`chart_group_field`) REFERENCES `field_configurations` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
