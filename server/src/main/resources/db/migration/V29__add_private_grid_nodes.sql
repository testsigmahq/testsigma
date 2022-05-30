DROP TABLE IF EXISTS `private_grid_nodes`;
CREATE TABLE `private_grid_nodes`
(
  `id`                    BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `node_name`             VARCHAR(255)                         DEFAULT NULL,
  `browser_list`          TEXT COLLATE utf8_unicode_ci,
  `grid_url`             VARCHAR(255)                         DEFAULT NULL,
  `created_date`          DATETIME                             DEFAULT CURRENT_TIMESTAMP,
  `updated_date`          DATETIME                             DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`))
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_unicode_ci;