DROP TABLE IF EXISTS `workspaces`;
CREATE TABLE `workspaces`
(
  `id`           BIGINT(20) NOT NULL AUTO_INCREMENT,
  `type`         VARCHAR(255)                         DEFAULT NULL,
  `description`  TEXT COLLATE utf8_unicode_ci,
  `name`         VARCHAR(250) COLLATE utf8_unicode_ci DEFAULT NULL,
  `is_demo`      TINYINT(1)                           DEFAULT '0',
  `created_date` DATETIME                             DEFAULT CURRENT_TIMESTAMP,
  `updated_date` DATETIME                             DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_workspaces_on_name` (`name`),
  UNIQUE KEY `index_workspace_on_type_and_is_demo` (`type`, `is_demo`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 12
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
