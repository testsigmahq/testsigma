DROP TABLE IF EXISTS `tags`;
CREATE TABLE `tags`
(
  `id`           BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name`         VARCHAR(2000) DEFAULT NULL,
  `type`         VARCHAR(255)  DEFAULT NULL,
  `count`        INT(11)       DEFAULT NULL,
  `created_date` DATETIME      DEFAULT CURRENT_TIMESTAMP,
  `updated_date` DATETIME      DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
