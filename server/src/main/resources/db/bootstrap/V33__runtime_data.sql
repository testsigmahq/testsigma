DROP TABLE IF EXISTS `runtime_data`;
CREATE TABLE `runtime_data`
(
  `id`               BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `test_plan_run_id` BIGINT(20)   NOT NULL,
  `session_id`       VARCHAR(256) NULL,
  `data`             JSON     DEFAULT NULL,
  `created_date`     DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_date`     DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `index_runtime_data_on_test_plan_run_id_and_session_id` (`test_plan_run_id`, `session_id`),
  CONSTRAINT `fk_test_plan_run_id_in_runtime_data_to_test_plan_runs` FOREIGN KEY (`test_plan_run_id`) REFERENCES `test_plan_results` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
