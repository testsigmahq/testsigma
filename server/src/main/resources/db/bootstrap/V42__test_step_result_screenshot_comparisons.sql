DROP TABLE IF EXISTS `test_step_result_screenshot_comparisons`;
CREATE TABLE `test_step_result_screenshot_comparisons`
(
  `id`                      BIGINT(20) NOT NULL AUTO_INCREMENT,
  `test_step_id`            BIGINT(20) NOT NULL,
  `test_case_result_id`     BIGINT(20)    DEFAULT NULL,
  `test_step_result_id`     BIGINT(20)    DEFAULT NULL,
  `test_step_screenshot_id` BIGINT(20)    DEFAULT NULL,
  `similarity_score`        DECIMAL(6, 3) DEFAULT NULL,
  `diff_coordinates`        LONGTEXT,
  `image_shape`             VARCHAR(32)   DEFAULT NULL,
  `error_message`           LONGTEXT,
  `created_date`            DATETIME      DEFAULT CURRENT_TIMESTAMP,
  `updated_date`            DATETIME      DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_test_step_id_and_test_step_result_id` (`test_step_id`, `test_step_result_id`),
  KEY `index_test_step_id` (`test_step_id`),
  KEY `index_test_step_result_id` (`test_step_result_id`),
  KEY `index_test_step_screenshot_id` (`test_step_screenshot_id`),
  KEY `index_test_case_result_id` (`test_case_result_id`),
  CONSTRAINT `fk_test_step_id_to_test_steps` FOREIGN KEY (`test_step_id`) REFERENCES `test_steps` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `fk_test_step_screenshot_id_to_test_step_screenshots` FOREIGN KEY (`test_step_screenshot_id`) REFERENCES `test_step_screenshots` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `fk_test_step_result_id_to_test_step_results` FOREIGN KEY (`test_step_result_id`) REFERENCES `test_step_results` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
