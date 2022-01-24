DROP TABLE IF EXISTS `test_cases`;
CREATE TABLE `test_cases`
(
  `id`                     BIGINT(20) NOT NULL AUTO_INCREMENT,
  `start_time`             DATETIME            DEFAULT NULL,
  `end_time`               DATETIME            DEFAULT NULL,
  `is_data_driven`         BIT(1)     NOT NULL DEFAULT 0,
  `is_step_group`      BIT(1)     NOT NULL DEFAULT 0,
  `priority_id`            BIGINT(20)          DEFAULT NULL,
  `requirement_id`         BIGINT(20)          DEFAULT NULL,
  `description`            TEXT                DEFAULT NULL,
  `name`                   VARCHAR(250)        DEFAULT NULL,
  `status`                 VARCHAR(100)        DEFAULT NULL,
  `type`                   BIGINT(20)          DEFAULT NULL,
  `test_data_id`           BIGINT(20)          DEFAULT NULL,
  `workspace_version_id` BIGINT(20)          DEFAULT NULL,
  `pre_requisite`          BIGINT(20)          DEFAULT NULL,
  `copied_from`            BIGINT(20)          DEFAULT NULL,
  `deleted`                BIT(1)              DEFAULT FALSE,
  `test_data_start_index`        INT(11)             DEFAULT 0,
  `test_data_end_index`    INT(11)             DEFAULT NULL,
  `last_run_id`            BIGINT(20)          DEFAULT NULL,
  `draft_at`               DATETIME            DEFAULT NULL,
  `obsolete_at`            DATETIME            DEFAULT NULL,
  `ready_at`               DATETIME            DEFAULT NULL,
  `created_date`           DATETIME            DEFAULT CURRENT_TIMESTAMP,
  `updated_date`           DATETIME            DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `index_on_test_cases_requirement_id` (`requirement_id`),
  KEY `index_on_test_cases_priority_id` (`priority_id`),
  KEY `index_on_test_cases_type` (`type`),
  KEY `index_on_test_cases_workspace_version_id` (`workspace_version_id`),
  KEY `index_on_test_cases_status` (`status`),
  KEY `index_on_test_cases_is_step_group` (`is_step_group`),
  CONSTRAINT `index_on_test_cases_requirement_id_and_name` UNIQUE (requirement_id, name),
  CONSTRAINT `fk_priority_id_in_test_cases_to_test_case_priorities` FOREIGN KEY (`priority_id`) REFERENCES `test_case_priorities` (`id`),
  CONSTRAINT `fk_type_in_test_cases_to_test_case_types` FOREIGN KEY (`type`) REFERENCES `test_case_types` (`id`),
  CONSTRAINT `fk_requirement_id_in_test_cases_to_requirements` FOREIGN KEY (`requirement_id`) REFERENCES `requirements` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `fk_test_data_id_in_test_cases_to_test_data` FOREIGN KEY (`test_data_id`) REFERENCES `test_data` (`id`) ON UPDATE NO ACTION
) ENGINE = InnoDB
  AUTO_INCREMENT = 35
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
