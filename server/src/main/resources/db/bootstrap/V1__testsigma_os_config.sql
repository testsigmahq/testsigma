DROP TABLE IF EXISTS `testsigma_os_config`;
CREATE TABLE `testsigma_os_config`
(
  `id`           BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_name`    VARCHAR(256),
  `access_key`   VARCHAR(1000),
  `created_date` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_date` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
