DROP TABLE IF EXISTS `test_case_data_driven_results`;
CREATE TABLE `test_case_data_driven_results`
(
  `id`                    BIGINT(20) NOT NULL AUTO_INCREMENT,
  `test_case_id`          BIGINT(20)   DEFAULT NULL,
  `test_data_name`        VARCHAR(250) DEFAULT NULL,
  `test_data`             TEXT,
  `test_device_result_id` BIGINT(20)   DEFAULT NULL,
  `test_case_result_id`   BIGINT(20),
  `iteration_result_id`   BIGINT(20),
  `created_date`          DATETIME    DEFAULT CURRENT_TIMESTAMP,
  `updated_date`          DATETIME    DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY `index_test_case_data_driven_results_on_test_case_id` (`test_case_id`),
  KEY `index_on_test_device_result_id_and_iteration_result_id` (`test_device_result_id`, `iteration_result_id`),
  CONSTRAINT fk_test_case_result_id_to_test_case_results FOREIGN KEY (test_case_result_id) REFERENCES test_case_results (id) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
