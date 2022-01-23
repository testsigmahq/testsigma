DROP TABLE IF EXISTS `test_suites`;
CREATE TABLE `test_suites`
(
  `id`             BIGINT(20) NOT NULL AUTO_INCREMENT,
  `action_id`      BIGINT(20)                           DEFAULT NULL,
  `workspace_version_id` BIGINT(20)                           DEFAULT NULL,
  `name`           VARCHAR(250)                         DEFAULT NULL,
  `pre_requisite`  BIGINT(20)                           DEFAULT NULL,
  `description`    TEXT                                 DEFAULT NULL,
  `copied_from`    BIGINT(20)                           DEFAULT null,
  `created_date`   DATETIME                             DEFAULT CURRENT_TIMESTAMP,
  `updated_date`   DATETIME                             DEFAULT CURRENT_TIMESTAMP,
  `entity_type`    VARCHAR(255) COLLATE utf8_unicode_ci,
  PRIMARY KEY (`id`),
  KEY `index_test_suites_on_workspace_version_id` (`workspace_version_id`),
  UNIQUE KEY `index_test_suites_on_workspace_version_id_and_name` (`workspace_version_id`, `name`),
  CONSTRAINT `fk_workspace_version_id_in_test_suites_to_workspace_versions` FOREIGN KEY (`workspace_version_id`) REFERENCES `workspace_versions` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB
  AUTO_INCREMENT = 23
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
