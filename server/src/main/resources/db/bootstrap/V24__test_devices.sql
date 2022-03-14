DROP TABLE IF EXISTS `test_devices`;
CREATE TABLE `test_devices`
(
  `id`                            BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `title`                         VARCHAR(250) NOT NULL,
  `test_plan_id`                  BIGINT(20)            DEFAULT NULL,
  `agent_id`                      BIGINT(50)            DEFAULT NULL,
  `device_id`                     BIGINT(20)            DEFAULT NULL,
  `browser`                       VARCHAR(100)          DEFAULT NULL,
  `platform_device_id`            BIGINT(20)            DEFAULT NULL,
  `udid`                          VARCHAR(255)          DEFAULT NULL,
  `app_upload_id`                 BIGINT(20)            DEFAULT NULL,
  `app_package`                   VARCHAR(255)          DEFAULT NULL,
  `app_activity`                  VARCHAR(255)          DEFAULT NULL,
  `app_url`                       VARCHAR(255)          DEFAULT NULL,
  `app_bundle_id`                 VARCHAR(255)          DEFAULT NULL,
  `app_path_type`                 VARCHAR(255)          DEFAULT NULL,
  `capabilities`                  VARCHAR(255)          DEFAULT NULL,
  `platform_screen_resolution_id` BIGINT(20)            DEFAULT NULL,
  `platform_browser_version_id`   BIGINT(20)            DEFAULT NULL,
  `platform_os_version_id`        BIGINT(20)            DEFAULT NULL,
  `disabled`                      BIT(1)       NOT NULL DEFAULT false,
  `match_browser_version`         TINYINT(1)            DEFAULT 0,
  `copied_from`                   BIGINT(20)            DEFAULT null,
  `create_session_at_case_level`  TINYINT(1)            DEFAULT 0 NOT NULL,
  `created_date`                  DATETIME              DEFAULT CURRENT_TIMESTAMP,
  `updated_date`                  DATETIME              DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `index_test_devices_on_test_plan_id` (`test_plan_id`),
  KEY `index_test_devices_on_app_upload_id` (`app_upload_id`),
  KEY `index_test_devices_on_agent_id` (`agent_id`),
  CONSTRAINT `fk_test_plan_id_in_test_devices_to_test_plans` FOREIGN KEY (`test_plan_id`) REFERENCES `test_plans` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `fk_app_upload_id_in_test_devices_to_uploads` FOREIGN KEY (`app_upload_id`) REFERENCES `uploads` (`id`) ON DELETE RESTRICT ON UPDATE NO ACTION,
  CONSTRAINT `fk_agent_id_in_test_devices_to_uploads` FOREIGN KEY (`agent_id`) REFERENCES `agents` (`id`) ON DELETE RESTRICT ON UPDATE NO ACTION
) ENGINE = InnoDB
  AUTO_INCREMENT = 59
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
