DROP TABLE IF EXISTS `user_preferences`;
CREATE TABLE `user_preferences`
(
  `id`                  BIGINT(20) NOT NULL AUTO_INCREMENT,
  `email`               VARCHAR(256),
  `version_id`          BIGINT(20) DEFAULT NULL,
  `test_case_filter_id` BIGINT(20) DEFAULT NULL,
  `created_date`        DATETIME   DEFAULT CURRENT_TIMESTAMP,
  `updated_date`        DATETIME   DEFAULT CURRENT_TIMESTAMP,
  `showed_github_star`  BIT(1)     DEFAULT b'0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_user_preferences_on_email` (`email`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
