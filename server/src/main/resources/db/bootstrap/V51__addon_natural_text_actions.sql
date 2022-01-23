DROP TABLE IF EXISTS `addon_natural_text_actions`;
CREATE TABLE `addon_natural_text_actions`
(
  `id`                   BIGINT(20)                           NOT NULL AUTO_INCREMENT,
  `addon_id`            BIGINT(20) DEFAULT NULL,
  `fully_qualified_name` VARCHAR(256) COLLATE utf8_unicode_ci NOT NULL,
  `natural_text`         VARCHAR(256) COLLATE utf8_unicode_ci NOT NULL,
  `description`          TEXT COLLATE utf8_unicode_ci         NOT NULL,
  `workspace_type`     VARCHAR(256) COLLATE utf8_unicode_ci NOT NULL,
  `deprecated`           TINYINT(1) DEFAULT '0',
  `created_date`         DATETIME   DEFAULT CURRENT_TIMESTAMP,
  `updated_date`         DATETIME   DEFAULT CURRENT_TIMESTAMP,
  `condition_type`       varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `index_addon_natural_text_actions_on_workspace_type` (`workspace_type`),
  KEY `index_addon_natural_text_actions_on_addon_id` (`addon_id`),
  CONSTRAINT `fk_addon_id_in_addon_natural_text_actions_to_addons` FOREIGN KEY (`addon_id`) REFERENCES `addons` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_unicode_ci;
