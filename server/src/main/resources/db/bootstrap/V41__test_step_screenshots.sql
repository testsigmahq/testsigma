DROP TABLE IF EXISTS `test_step_screenshots`;
CREATE TABLE `test_step_screenshots`
(
  `id`                    BIGINT(20) NOT NULL AUTO_INCREMENT,
  `test_step_id`          BIGINT(20) NOT NULL,
  `test_step_result_id`   BIGINT(20)    DEFAULT NULL,
  `test_device_result_id` BIGINT(20) NOT NULL,
  `testcase_result_id`    BIGINT(20) NOT NULL,
  `base_image_size`       VARCHAR(50)   DEFAULT NULL,
  `entity_type`           VARCHAR(30) DEFAULT NULL,
  `test_data_set_name`    VARCHAR(1000) DEFAULT NULL,
  `test_data_id`          BIGINT(20)    DEFAULT NULL,
  `base_image_name`       TEXT,
  `screen_resolution`     VARCHAR(100)  DEFAULT NULL,
  `browser`               VARCHAR(50)   DEFAULT NULL,
  `browser_version`       DOUBLE(20, 2) DEFAULT NULL,
  `ignored_coordinates`   LONGTEXT,
  `device_os_version`     VARCHAR(16)   DEFAULT NULL,
  `device_name`           VARCHAR(128)  DEFAULT NULL,
  `created_date`          DATETIME      DEFAULT CURRENT_TIMESTAMP,
  `updated_date`          DATETIME      DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `index_test_step_screenshots_on_test_step_id` (`test_step_id`),
  KEY `index_test_step_screenshots_on_test_device_result_id` (`test_device_result_id`),
  KEY `index_test_step_screenshots_on_testcase_result_id` (`testcase_result_id`),
  KEY `index_test_step_screenshots_on_test_step_result_id` (`test_step_result_id`),
  CONSTRAINT `fk_test_device_result_id_in_screenshots_to_test_device_results` FOREIGN KEY (`test_device_result_id`) REFERENCES `test_device_results` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `fk_test_step_id_in_test_step_screenshots_to_test_steps` FOREIGN KEY (`test_step_id`) REFERENCES `test_steps` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `fk_test_case_result_id_in_screenshots_to_test_case_results` FOREIGN KEY (`testcase_result_id`) REFERENCES `test_case_results` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `fk_test_step_result_id_in_screenshots_to_test_step_results` FOREIGN KEY (`test_step_result_id`) REFERENCES `test_step_results` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
