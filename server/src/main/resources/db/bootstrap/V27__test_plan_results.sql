DROP TABLE IF EXISTS `test_plan_results`;
CREATE TABLE `test_plan_results`
(
  `id`                         BIGINT(20) NOT NULL AUTO_INCREMENT,
  `test_plan_id`               BIGINT(20) NOT NULL,
  `start_time`                 TIMESTAMP  NULL DEFAULT NULL,
  `end_time`                   TIMESTAMP  NULL DEFAULT NULL,
  `duration`                   BIGINT(20)      DEFAULT NULL,
  `result`                     VARCHAR(100)    DEFAULT NULL,
  `status`                     VARCHAR(100)    DEFAULT NULL,
  `message`                    TEXT,
  `build_no`                   VARCHAR(250),
  `environment_id`             BIGINT(20),
  `test_plan_details`          JSON,
  `is_in_progress`             BIT(1)          DEFAULT FALSE,
  `total_count`                BIGINT(20)      DEFAULT 0,
  `failed_count`               BIGINT(20)      DEFAULT 0,
  `passed_count`               BIGINT(20)      DEFAULT 0,
  `aborted_count`              BIGINT(20)      DEFAULT 0,
  `stopped_count`              BIGINT(20)      DEFAULT 0,
  `not_executed_count`         BIGINT(20)      DEFAULT 0,
  `queued_count`               BIGINT(20)      DEFAULT 0,
  `is_visually_passed`         BIT(1)          DEFAULT NULL,
  `re_run_type`                VARCHAR(255)    DEFAULT NULL,
  `re_run_parent_id`           BIGINT(20)      DEFAULT NULL,
  `triggered_type`             VARCHAR(100)    DEFAULT NULL,
  `scheduled_id`               BIGINT(20)      DEFAULT NULL,
  `created_date`               DATETIME        DEFAULT CURRENT_TIMESTAMP,
  `updated_date`               DATETIME        DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `index_test_plan_results_on_test_plan_id` (`test_plan_id`),
  KEY `index_test_plan_results_on_environment_id` (`environment_id`),
  KEY `index_test_plan_results_on_status_index` (`status`),
  KEY `index_test_plan_results_on_result_index` (`result`),
  KEY `index_test_plan_results_on_start_time_index` (`start_time`),
  KEY `index_test_plan_results_on_re_run_parent_id_index` (`re_run_parent_id`),
  KEY `index_test_plan_results_on_scheduled_id` (`scheduled_id`),
  CONSTRAINT `fk_test_plan_id_in_test_plan_results_to_test_plans` FOREIGN KEY (`test_plan_id`) REFERENCES `test_plans` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `fk_environment_id_in_test_plan_results_to_environments` FOREIGN KEY (environment_id) REFERENCES environments (id) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_scheduled_id_in_test_plan_results_to_schedule_test_plans` FOREIGN KEY (`scheduled_id`) REFERENCES schedule_test_plans (id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB
  AUTO_INCREMENT = 221
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
