DROP TABLE IF EXISTS `backup_details`;
CREATE TABLE `backup_details`
(
  `id`                             BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name`                           VARCHAR(1000),
  `xml_name`                       VARCHAR(1000) DEFAULT NULL,
  `status`                         INT(4),
  `message`                        VARCHAR(2000),
  `is_test_case_enabled`           TINYINT(1)    DEFAULT '0',
  `is_test_step_enabled`           TINYINT(1)    DEFAULT '0',
  `is_rest_step_enabled`           TINYINT(1)    DEFAULT '0',
  `is_upload_enabled`              TINYINT(1)    DEFAULT '0',
  `is_test_case_priority_enabled`  TINYINT(1)    DEFAULT '0',
  `is_test_case_type_enabled`      TINYINT(1)    DEFAULT '0',
  `is_element_enabled`             TINYINT(1)    DEFAULT '0',
  `is_element_screen_name_enabled` TINYINT(1)    DEFAULT '0',
  `is_test_data_enabled`           TINYINT(1)    DEFAULT '0',
  `is_attachment_enabled`          TINYINT(1)    DEFAULT '0',
  `is_agent_enabled`               TINYINT(1)    DEFAULT '0',
  `is_test_plan_enabled`           TINYINT(1)    DEFAULT '0',
  `is_test_device_enabled`         TINYINT(1)    DEFAULT '0',
  `is_suites_enabled`              TINYINT(1)    DEFAULT '0',
  `is_label_enabled`               TINYINT(1)    DEFAULT '0',
  `workspace_version_id`           BIGINT(20)    DEFAULT '0',
  `filter_id`                      BIGINT(20)    DEFAULT NULL,
  `entity_id`                      VARCHAR(50)   DEFAULT NULL,
  `skip_entity_exists`             BIT(1)        DEFAULT NULL,
  `action_type`                    VARCHAR (1000) DEFAULT NULL,
  `affected_cases_list_path`       VARCHAR (1000) DEFAULT NULL,
  `created_date`                   DATETIME      DEFAULT CURRENT_TIMESTAMP,
  `updated_date`                   DATETIME      DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;

