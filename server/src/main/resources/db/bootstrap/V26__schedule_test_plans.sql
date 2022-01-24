DROP TABLE IF EXISTS `schedule_test_plans`;
CREATE TABLE `schedule_test_plans`
(
  `id`            BIGINT       NOT NULL AUTO_INCREMENT,
  `test_plan_id`  BIGINT       NOT NULL,
  `name`          VARCHAR(125) NULL,
  `comments`      VARCHAR(45)  NULL,
  `schedule_type` VARCHAR(255) NULL,
  `schedule_time` TIMESTAMP    NULL,
  `status`        VARCHAR(255) NULL,
  `queue_status`  VARCHAR(255) DEFAULT 'IN_PROGRESS',
  `created_date`  DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `updated_date`  DATETIME     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `index_schedule_test_plans_on_test_plan_id_and_status` (`test_plan_id`, `status`),
  CONSTRAINT `fk_test_plan_id_in_schedule_test_plans_to_test_plans` FOREIGN KEY (`test_plan_id`) REFERENCES `test_plans` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
