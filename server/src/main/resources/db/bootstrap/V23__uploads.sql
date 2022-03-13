DROP TABLE IF EXISTS `uploads`;
CREATE TABLE `uploads`
(
  `id`             BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `name`           VARCHAR(256) DEFAULT NULL,
  `path`           TEXT         DEFAULT NULL,
  `file_name`      VARCHAR(256) DEFAULT NULL,
  `type`           VARCHAR(100) NOT NULL,
  `version`        VARCHAR(256) DEFAULT NULL,
  `file_size`      INT(11),
  `workspace_id` BIGINT(20)   DEFAULT NULL,
  `created_date`   DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `updated_date`   DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `upload_status`  VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_uploads_on_workspace_id_and_name` (`workspace_id`, `name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;


