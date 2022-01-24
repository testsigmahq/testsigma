DROP TABLE IF EXISTS `suggestion_result_mapping`;
CREATE TABLE `suggestion_result_mapping`
(
  `id`             BIGINT(20) NOT NULL AUTO_INCREMENT,
  `step_result_id` BIGINT(20)   DEFAULT NULL,
  `suggestion_id`  INT(11)      DEFAULT NULL,
  `result`         VARCHAR(255) DEFAULT NULL,
  `message`        TEXT,
  `meta_data`      JSON         DEFAULT NULL,
  `created_date`   DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `updated_date`   DATETIME     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 7
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
