DROP TABLE IF EXISTS `test_step_results`;
CREATE TABLE `test_step_results`
(
  `id`                    BIGINT(20) NOT NULL AUTO_INCREMENT,
  `test_device_result_id` BIGINT(20) NOT NULL,
  `test_case_id`          BIGINT(20) NOT NULL,
  `step_id`               BIGINT(20) NOT NULL,
  `step_group_id`     BIGINT(20)      DEFAULT NULL,
  `result`                VARCHAR(100)    DEFAULT NULL,
  `error_code`            INT(11)         DEFAULT NULL,
  `message`               TEXT,
  `metadata`              LONGTEXT  DEFAULT NULL,
  `start_time`            TIMESTAMP  NULL DEFAULT NULL,
  `end_time`              TIMESTAMP  NULL DEFAULT NULL,
  `duration`              BIGINT(20)      DEFAULT NULL,
  `test_case_result_id`   BIGINT(20),
  `step_group_result_id`  BIGINT(20),
  `screenshot_name`       TEXT,
  `parent_result_id`      BIGINT(20)      DEFAULT NULL,
  `web_driver_exception`  TEXT,
  `priority`              VARCHAR(255),
  `test_step_details`     JSON,
  `element_details` JSON,
  `test_data_details`     JSON            DEFAULT NULL,
  `visual_enabled`              tinyint(1) DEFAULT '0',
  `wait_time`             BIGINT(20)      DEFAULT NULL,
  `addon_test_data`     JSON            DEFAULT NULL,
  `addon_elements`      JSON            DEFAULT NULL,
  `addon_action_logs`      LONGTEXT COLLATE utf8_unicode_ci,
  `created_date`          DATETIME        DEFAULT CURRENT_TIMESTAMP,
  `updated_date`          DATETIME        DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY `index_test_step_results_on_step_id` (`step_id`),
  KEY `index_test_step_results_on_test_device_result_id` (`test_device_result_id`),
  KEY `index_test_step_results_on_test_case_result_id` (`test_case_result_id`),
  KEY `index_test_step_results_on_step_group_result_id` (`step_group_result_id`),
  CONSTRAINT `fk_env_run_id_in_test_step_results_to_test_device_results` FOREIGN KEY (`test_device_result_id`) REFERENCES `test_device_results` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `fk_test_case_result_id_in_test_step_results_to_test_case_results` FOREIGN KEY (`test_case_result_id`) REFERENCES `test_case_results` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB
  AUTO_INCREMENT = 14884
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
