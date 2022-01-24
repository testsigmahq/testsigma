DROP TABLE IF EXISTS `test_case_priorities`;
CREATE TABLE `test_case_priorities`
(
  `id`             BIGINT(20) NOT NULL AUTO_INCREMENT,
  `display_name`   VARCHAR(250) DEFAULT NULL,
  `name`           VARCHAR(250) DEFAULT NULL,
  `workspace_id` BIGINT(20) NOT NULL,
  `created_date`   DATETIME      DEFAULT CURRENT_TIMESTAMP,
  `updated_date`   DATETIME      DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_test_case_priorities_on_name_and_workspace_id` (`name`, `workspace_id`),
  KEY `index_test_case_priorities_on_workspace_id` (`workspace_id`),
  CONSTRAINT `fk_workspace_id_in_test_case_priorities_to_workspaces` FOREIGN KEY (`workspace_id`) REFERENCES `workspaces` (`id`) ON UPDATE NO ACTION
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
