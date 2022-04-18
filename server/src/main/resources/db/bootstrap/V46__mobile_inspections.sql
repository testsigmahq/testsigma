DROP TABLE IF EXISTS `mobile_inspections`;
CREATE TABLE `mobile_inspections`
(
  `id`                    BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `platform`              VARCHAR(100) NULL,
  `agent_device_id`       BIGINT(20)   NULL,
  `status`                VARCHAR(255) NULL,
  `lab_type`              VARCHAR(255) NULL,
  `platform_device_id`    BIGINT(20)   NULL,
  `app_activity`          VARCHAR(256) NULL,
  `bundle_id`             VARCHAR(256) NULL,
  `upload_version_id`     bigint(20) NULL,
  `capabilities`          JSON     DEFAULT NULL,
  `cookies`               JSON     DEFAULT NULL,
  `session_id`            VARCHAR(256) NULL,
  `application_package`   VARCHAR(256) NULL,
  `application_path_type` VARCHAR(255) NULL,
  `started_at`            DATETIME DEFAULT NULL,
  `finished_at`           DATETIME DEFAULT NULL,
  `last_active_at`        DATETIME DEFAULT NULL,
  `created_date`          DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_date`          DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY index_mobile_inspections_on_status_and_last_active_at (status, last_active_at),
  KEY index_mobile_inspections_on_status_and_lab_type (status, lab_type)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
