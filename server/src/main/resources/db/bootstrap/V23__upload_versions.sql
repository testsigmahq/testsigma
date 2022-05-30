DROP TABLE IF EXISTS `upload_versions`;
CREATE TABLE `upload_versions`
(
  `id`             BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `name`           VARCHAR(256) DEFAULT NULL,
  `path`           TEXT         DEFAULT NULL,
  `file_name`      VARCHAR(256) DEFAULT NULL,
  `type`           VARCHAR(100) NOT NULL,
  `version`        VARCHAR(256) DEFAULT NULL,
  `file_size`      INT(11),
  `upload_id`      BIGINT(20) DEFAULT NULL,
  `workspace_id`   BIGINT(20)   DEFAULT NULL,
  `imported_id`    BIGINT(20) DEFAULT NULL,
  `version_name`   VARCHAR(100) DEFAULT null,
  `bundle_id`      VARCHAR(100) DEFAULT null,
  `activity`       VARCHAR(100) DEFAULT null ,
  `package_name`   VARCHAR(100) DEFAULT null,
  `created_date`   DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `updated_date`   DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `upload_status`  VARCHAR(100) DEFAULT NULL,
  `last_uploaded_time`   DATETIME      DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_uploads_on_workspace_id_and_name` (`workspace_id`, `name`),
  UNIQUE KEY `index_uploads_on_upload_id_and_name` (`upload_id`, `name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;


