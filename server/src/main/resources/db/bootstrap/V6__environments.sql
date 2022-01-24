DROP TABLE IF EXISTS `environments`;
CREATE TABLE `environments`
(
  `id`           BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name`         VARCHAR(250) DEFAULT NULL,
  `description`  TEXT         DEFAULT NULL,
  `parameters`   TEXT,
  `passwords`    JSON         DEFAULT NULL,
  `created_date`   DATETIME   DEFAULT CURRENT_TIMESTAMP,
  `updated_date`   DATETIME   DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_environments_on_name` (`name`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 4
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
