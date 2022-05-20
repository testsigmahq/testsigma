DROP TABLE IF EXISTS `test_plans`;
CREATE TABLE `test_plans`
(
  `id`                                         BIGINT(20) NOT NULL AUTO_INCREMENT,
  `workspace_version_id`                     BIGINT(20)                            DEFAULT NULL,
  `description`                                TEXT COLLATE utf8_unicode_ci,
  `element_time_out`                           INT(11) UNSIGNED                      DEFAULT '0',
  `environment_id`                             BIGINT(20)                            DEFAULT NULL,
  `name`                                       VARCHAR(250) COLLATE utf8_unicode_ci  DEFAULT NULL,
  `page_time_out`                              INT(11) UNSIGNED                      DEFAULT '0',
  `screenshot`                                 VARCHAR(255)                          DEFAULT NULL,
  `recovery_action`                            VARCHAR(255)                          NOT NULL,
  `on_aborted_action`                          VARCHAR(255)                          NOT NULL,
  `re_run_on_failure`                          VARCHAR(255)                          NOT NULL,
  `on_suite_pre_requisite_failed`              VARCHAR(255)                          NOT NULL,
  `on_testcase_pre_requisite_failed`           VARCHAR(255)                          NOT NULL,
  `on_step_pre_requisite_failed`               VARCHAR(255)                          NOT NULL,
  `retry_session_timeout`                      INT(11)                               DEFAULT 0,
  `retry_session_creation`                     BIT(1)                                DEFAULT b'0',
  `test_lab_type`                              VARCHAR(255)                          DEFAULT NULL,
  `test_plan_type`                             VARCHAR(255)                          DEFAULT 'DISTRIBUTED',
  `last_run_id`                                BIGINT(20)                            DEFAULT NULL,
  `match_browser_version`                      TINYINT(1)                            DEFAULT '0',
  `copied_from`                                BIGINT(20)                            default null,
  `entity_type`                                VARCHAR(255) COLLATE utf8_unicode_ci  DEFAULT 'TEST_PLAN',
  `imported_id`                                BIGINT(20) DEFAULT NULL,
  `created_date`                               DATETIME                              DEFAULT CURRENT_TIMESTAMP,
  `updated_date`                               DATETIME                              DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_test_plans_on_workspace_version_id_and_name` (`workspace_version_id`, `name`),
  KEY `index_test_plans_on_workspace_version_id` (`workspace_version_id`),
  KEY `index_test_plans_on_environment_id` (`environment_id`),
  CONSTRAINT `fk_environment_id_in_test_plans_to_environments` FOREIGN KEY (`environment_id`) REFERENCES `environments` (`id`) ON UPDATE NO ACTION,
  CONSTRAINT `fk_workspace_version_id_in_test_plans_to_workspace_versions` FOREIGN KEY (`workspace_version_id`) REFERENCES `workspace_versions` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB
  AUTO_INCREMENT = 178
  DEFAULT CHARSET = utf8
  COLLATE = utf8_unicode_ci;
