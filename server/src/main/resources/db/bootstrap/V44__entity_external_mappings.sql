DROP TABLE IF EXISTS entity_external_mappings;
CREATE TABLE `entity_external_mappings`
(
  `id`                  bigint(20) NOT NULL AUTO_INCREMENT,
  `entity_type`         varchar(255) DEFAULT NUll,
  `entity_id` bigint(20) NOT NULL,
  `application_id`      bigint(20) NOT NULL,
  `external_id`         varchar(255) DEFAULT NULL,
  `misc`                text     DEFAULT NULL,
  `push_failed`         tinyint(1) DEFAULT NULl,
  `assets_push_failed`  TINYINT(1) DEFAULT NULL,
  `message`             text DEFAULT NULL,
  `created_date`      DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `updated_date`      DATETIME     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY `fk_application_id` (`application_id`),
  CONSTRAINT `fk_application_id_to_integrations` FOREIGN KEY (`application_id`) REFERENCES integrations (id) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;

