DROP TABLE IF EXISTS `test_suite_cases`;
CREATE TABLE `test_suite_cases`
(
  `id`           BIGINT(20) NOT NULL AUTO_INCREMENT,
  `suite_id`     BIGINT(20) NOT NULL,
  `test_case_id` BIGINT(20) NOT NULL,
  `position`     BIGINT(20) DEFAULT NULL,
  `imported_id`    BIGINT(20) DEFAULT NULL,
  `created_date` DATETIME   DEFAULT CURRENT_TIMESTAMP,
  `updated_date` DATETIME   DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `index_test_suite_cases_on_suite_id` (`suite_id`),
  KEY `index_test_suite_cases_on_test_case_id` (`test_case_id`),
  CONSTRAINT `fk_suite_id_in_test_suite_cases_to_test_suites` FOREIGN KEY (`suite_id`) REFERENCES `test_suites` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `fk_test_case_id_in_test_suite_cases_to_test_cases` FOREIGN KEY (`test_case_id`) REFERENCES `test_cases` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
