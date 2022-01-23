DROP TABLE IF EXISTS `list_filters`;
CREATE TABLE `list_filters`
(
  `id`           BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `name`         VARCHAR(255) NOT NULL,
  `version_id`   BIGINT(20)   DEFAULT NULL,
  `type`         VARCHAR(255) DEFAULT NULL,
  `query_hash`   TEXT COLLATE utf8_unicode_ci,
  `is_public`    BIT(1)       DEFAULT b'0',
  `is_default`   BIT(1)       DEFAULT b'0',
  `created_date` DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `updated_date` DATETIME     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_list_filters_on_name_and_type_and_is_public` (`name`, `type`, `is_public`),
  CONSTRAINT `fk_version_id_in_list_filters_to_workspace_versions` FOREIGN KEY (`version_id`) REFERENCES `workspace_versions` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_unicode_ci;
