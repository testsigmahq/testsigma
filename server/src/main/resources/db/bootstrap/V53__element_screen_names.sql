DROP TABLE IF EXISTS `element_screen_names`;
CREATE TABLE `element_screen_names`
(
  `id`           BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `name`         VARCHAR(250) NOT NULL,
  `version_id`   BIGINT(20)   NOT NULL,
  `copied_from`  BIGINT(20) DEFAULT NULL,
  `imported_id`  BIGINT(20) DEFAULT NULL,
  `created_date` DATETIME   DEFAULT CURRENT_TIMESTAMP,
  `updated_date` DATETIME   DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_elements_screen_names_on_version_id_name` (`version_id`, `name`),
  CONSTRAINT `fk_version_id_in_element_screen_names_to_workspace_versions` FOREIGN KEY (`version_id`) REFERENCES `workspace_versions` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_unicode_ci;
