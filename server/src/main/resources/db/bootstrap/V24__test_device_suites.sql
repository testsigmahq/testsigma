DROP TABLE IF EXISTS `test_device_suites`;
CREATE TABLE `test_device_suites`
(
  `id`             BIGINT(20) NOT NULL AUTO_INCREMENT,
  `test_device_id` BIGINT(20) DEFAULT NULL,
  `suite_id`       BIGINT(20) DEFAULT NULL,
  `order_id`       INT(11)    DEFAULT NULL,
  `created_date`   DATETIME   DEFAULT CURRENT_TIMESTAMP,
  `updated_date`   DATETIME   DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `index_test_device_suites_on_test_device_id` (`test_device_id`),
  KEY `index_test_device_suites_on_suite_id` (`suite_id`),
  UNIQUE KEY `index_test_device_suites_on_test_device_id_and_suite_id` (`test_device_id`, `suite_id`),
  CONSTRAINT `fk_test_device_id_in_test_device_suites_to_test_devices` FOREIGN KEY (`test_device_id`) REFERENCES `test_devices` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `fk_suite_id_in_test_device_suites_to_test_suites` FOREIGN KEY (`suite_id`) REFERENCES `test_suites` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
