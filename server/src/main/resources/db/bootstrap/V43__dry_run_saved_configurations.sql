DROP TABLE IF EXISTS `adhoc_run_configurations`;
CREATE TABLE `adhoc_run_configurations`
(
  `id`                            BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `config_name`                   VARCHAR(255) NOT NULL,
  `workspace_type`                VARCHAR(255) DEFAULT NULL,
  `page_timeout`                  INT(10)      DEFAULT NULL,
  `type`                          INT(3)       DEFAULT NULL,
  `capture_screenshots`           INT(11)      DEFAULT NULL,
  `environment_id`                VARCHAR(255) DEFAULT NULL,
  `element_timeout`               INT(10)      DEFAULT NULL,
  `desired_capabilities`          TEXT         DEFAULT NULL,
  `browser`                       VARCHAR(255) DEFAULT NULL,
  `agent_id`                      BIGINT(10)   DEFAULT NULL,
  `app_name`                      VARCHAR(255) DEFAULT NULL,
  `platform_os_version_id`        BIGINT(20)   DEFAULT NULL,
  `platform_browser_version_id`   BIGINT(20)   DEFAULT NULL,
  `platform_screen_resolution_id` BIGINT(20)   DEFAULT NULL,
  `platform_device_id`            BIGINT(20)   DEFAULT NULL,
  `device_name`                   VARCHAR(255) DEFAULT NULL,
  `udid`                          VARCHAR(255) DEFAULT NULL,
  `app_package`                   VARCHAR(255) DEFAULT NULL,
  `app_activity`                  VARCHAR(255) DEFAULT NULL,
  `app_upload_id`                 VARCHAR(255) DEFAULT NULL,
  `app_url`                       VARCHAR(255) DEFAULT NULL,
  `app_bundle_id`                 VARCHAR(255) DEFAULT NULL,
  `device_id`                     BIGINT(20)   DEFAULT NULL,
  `app_path_type`                 VARCHAR(255) DEFAULT NULL,
  `created_date`                  DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `updated_date`                  DATETIME     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_adhoc_run_configurations_on_work_type_and_config_name` (`workspace_type`, `config_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
