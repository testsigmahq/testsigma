DROP TABLE IF EXISTS `addon_natural_text_action_parameters`;
CREATE TABLE `addon_natural_text_action_parameters`
(
  `id`             BIGINT(20)                           NOT NULL AUTO_INCREMENT,
  `addon_natural_text_action_id`  BIGINT(20) DEFAULT NULL,
  `parameter_type` VARCHAR(256) COLLATE utf8_unicode_ci NOT NULL,
  `reference`      VARCHAR(256) COLLATE utf8_unicode_ci NOT NULL,
  `name`           VARCHAR(256) COLLATE utf8_unicode_ci NOT NULL,
  `description`    TEXT COLLATE utf8_unicode_ci         NOT NULL,
  `created_date`   DATETIME   DEFAULT CURRENT_TIMESTAMP,
  `updated_date`   DATETIME   DEFAULT CURRENT_TIMESTAMP,
  `allowed_values` mediumtext COLLATE utf8_unicode_ci,
  PRIMARY KEY (`id`),
  KEY `index_addon_natural_text_action_parameters_on_text_action_id` (`addon_natural_text_action_id`),
  CONSTRAINT `fk_addon_natural_text_action_id_to_addon_natural_text_actions` FOREIGN KEY (`addon_natural_text_action_id`) REFERENCES `addon_natural_text_actions` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_unicode_ci;
