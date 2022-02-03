DROP TABLE IF EXISTS `rest_step_details`;
CREATE TABLE `rest_step_details`
(
  `id`                    BIGINT(20)    NOT NULL AUTO_INCREMENT,
  `step_id`               BIGINT(20)    NOT NULL,
  `url`                   TEXT          NOT NULL,
  `method`                VARCHAR(250)  NOT NULL,
  `request_headers`       TEXT,
  `payload`               MEDIUMTEXT,
  `status`                VARCHAR(2000) NOT NULL,
  `header_compare_type`   VARCHAR(250)  NOT NULL,
  `response_headers`      TEXT,
  `response_compare_type` VARCHAR(250)  NOT NULL,
  `response`              TEXT,
  `store_metadata`        BIT(1)        NULL,
  `expected_result_type`  VARCHAR(40)   NOT NULL,
  `header_runtime_data`   JSON,
  `body_runtime_data`     JSON,
  `follow_redirects`      BIT(1)       DEFAULT TRUE,
  `authorization_type`    VARCHAR(255) DEFAULT 'NONE',
  `authorization_value`   JSON,
  `created_date`           DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `updated_date`           DATETIME     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `index_rest_step_details_on_step_id` (`step_id`),
  CONSTRAINT `fk_step_id_in_rest_step_details_to_test_steps` FOREIGN KEY (`step_id`) REFERENCES `test_steps` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB
  AUTO_INCREMENT = 16
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
