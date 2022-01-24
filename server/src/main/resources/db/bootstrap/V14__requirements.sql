DROP TABLE IF EXISTS `requirements`;
CREATE TABLE `requirements`
(
  `id`                       BIGINT(20) NOT NULL AUTO_INCREMENT,
  `workspace_version_id`   BIGINT(20)   DEFAULT NULL,
  `description`              TEXT         DEFAULT NULL,
  `name`                     VARCHAR(250) DEFAULT NULL,
  `copied_from`              BIGINT(20)   DEFAULT NULL,
  `created_date`             DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `updated_date`             DATETIME     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `index_requirements_on_workspace_version_id` (`workspace_version_id`),
  UNIQUE KEY `index_requirements_on_workspace_version_id_and_name` (`workspace_version_id`, `name`),
  CONSTRAINT `fk_workspace_version_id_in_requirements_to_workspace_versions` FOREIGN KEY (`workspace_version_id`) REFERENCES `workspace_versions` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB
  AUTO_INCREMENT = 25
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
