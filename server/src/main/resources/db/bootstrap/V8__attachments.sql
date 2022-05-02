DROP TABLE IF EXISTS `attachments`;
CREATE TABLE `attachments`
(
  `id`           BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `entity`       VARCHAR(255) NOT NULL,
  `entity_id`    BIGINT(20)   NOT NULL,
  `type`         INT(11)      NOT NULL DEFAULT 1,
  `path`         TEXT         NOT NULL,
  `name`         VARCHAR(250) NOT NULL,
  `description`  TEXT,
  `imported_id`  BIGINT(20) DEFAULT NULL,
  `created_date` DATETIME              DEFAULT CURRENT_TIMESTAMP,
  `updated_date` DATETIME              DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT index_attachments_on_entity_and_entity_id_and_name UNIQUE (`entity`, `entity_id`, `name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;


