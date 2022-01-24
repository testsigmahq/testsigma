DROP TABLE IF EXISTS `workspace_versions`;
CREATE TABLE `workspace_versions`
(
  `id`             BIGINT(20) NOT NULL AUTO_INCREMENT,
  `workspace_id`   BIGINT(20)                           DEFAULT NULL,
  `description`    TEXT COLLATE utf8_unicode_ci,
  `version_name`   VARCHAR(250) COLLATE utf8_unicode_ci DEFAULT NULL,
  `copied_from`    BIGINT(20)                           DEFAULT NULL,
  `created_date`   DATETIME                             DEFAULT CURRENT_TIMESTAMP,
  `updated_date`   DATETIME                             DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_workspace_versions_on_workspace_id_and_version_name` (`workspace_id`, `version_name`),
  KEY `index_workspace_versions_on_workspace_id` (`workspace_id`),
  CONSTRAINT `fk_workspace_id_in_workspace_versions_to_workspaces` FOREIGN KEY (`workspace_id`) REFERENCES `workspaces` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB
  AUTO_INCREMENT = 12
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
