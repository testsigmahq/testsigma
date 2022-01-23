DROP TABLE IF EXISTS `natural_text_actions`;
CREATE TABLE `natural_text_actions`
(
  `id`              BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `workspace_type`        VARCHAR(255) NOT NULL,
  `natural_text`    TEXT         NOT NULL,
  `data`            TEXT         DEFAULT NULL,
  `display_name`    VARCHAR(250) NOT NULL,
  `snippet_class`   VARCHAR(256) DEFAULT NULL,
  `action`          TEXT         DEFAULT NULL,
  `created_date`    DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `updated_date`    DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `allowed_values` mediumtext COLLATE utf8_unicode_ci,
  `condition_type`       varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
