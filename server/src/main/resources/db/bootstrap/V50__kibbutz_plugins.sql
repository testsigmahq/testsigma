DROP TABLE IF EXISTS `addons`;
CREATE TABLE `addons`
(
  `id`                                   BIGINT(20)                           NOT NULL AUTO_INCREMENT,
  `name`                                 VARCHAR(256) COLLATE utf8_unicode_ci NOT NULL,
  `version`                              VARCHAR(256) COLLATE utf8_unicode_ci NOT NULL,
  `description`                          TEXT COLLATE utf8_unicode_ci         NOT NULL,
  `external_unique_id`                   VARCHAR(256) COLLATE utf8_unicode_ci NOT NULL,
  `external_installed_version_unique_id` VARCHAR(256) COLLATE utf8_unicode_ci NOT NULL,
  `modified_hash`                        VARCHAR(256) COLLATE utf8_unicode_ci NOT NULL,
  `status`                               VARCHAR(256) DEFAULT NULL,
  `created_date`                         DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `updated_date`                         DATETIME     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `index_addons_on_status_and_external_unique_id` (`status`, `external_unique_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_unicode_ci;



DROP TABLE IF EXISTS `kibbutz_plugin_test_data_functions`;
CREATE TABLE `kibbutz_plugin_test_data_functions`
(
  `id`                   bigint(20)                           NOT NULL AUTO_INCREMENT,
  `addon_id`            bigint(20) DEFAULT NULL,
  `fully_qualified_name` varchar(256) COLLATE utf8_unicode_ci NOT NULL,
  `display_name`         varchar(256) COLLATE utf8_unicode_ci NOT NULL,
  `description`          longtext COLLATE utf8_unicode_ci NOT NULL,
  `deprecated`           tinyint(4) DEFAULT '0',
  `created_by_id`        bigint(20) DEFAULT NULL,
  `updated_by_id`        bigint(20) DEFAULT NULL,
  `created_date`         datetime   DEFAULT NULL,
  `updated_date`         datetime   DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_addon_id_in_kibbutz_test_data_functions` (`addon_id`),
  CONSTRAINT `fk_addon_id_in_kibbutz_test_data_functions` FOREIGN KEY (`addon_id`) REFERENCES `addons` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_unicode_ci;


DROP TABLE IF EXISTS `kibbutz_plugin_test_data_function_parameters`;
CREATE TABLE `kibbutz_plugin_test_data_function_parameters`
(
  `id`             bigint(20)                           NOT NULL AUTO_INCREMENT,
  `test_data_function_id`  bigint(20) DEFAULT NULL,
  `parameter_type` varchar(256) COLLATE utf8_unicode_ci,
  `reference`      varchar(256) COLLATE utf8_unicode_ci NOT NULL,
  `name`           varchar(256) COLLATE utf8_unicode_ci NOT NULL,
  `description`    longtext COLLATE utf8_unicode_ci NOT NULL,
  `created_by_id`  bigint(20) DEFAULT NULL,
  `updated_by_id`  bigint(20) DEFAULT NULL,
  `created_date`   datetime   DEFAULT NULL,
  `updated_date`   datetime   DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_test_data_function_id_in_kibbutz_test_data_function_params` (`test_data_function_id`),
  CONSTRAINT `fk_test_data_function_id_in_kibbutz_test_data_function_params` FOREIGN KEY (`test_data_function_id`) REFERENCES `kibbutz_plugin_test_data_functions` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_unicode_ci;
