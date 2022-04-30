DROP TABLE IF EXISTS `elements`;
CREATE TABLE `elements`
(
  `id`                     BIGINT(20) NOT NULL AUTO_INCREMENT,
  `workspace_version_id` BIGINT(20)    DEFAULT NULL,
  `locator_value`          VARCHAR(2054) DEFAULT NULL,
  `element_name`           VARCHAR(250)  DEFAULT NULL,
  `element_type`           INT(11)       DEFAULT NULL,
  `create_type`            VARCHAR(100)  DEFAULT 'CHROME',
  `locator_type`           VARCHAR(100)  DEFAULT NULL,
  `metadata`               JSON,
  `attributes`             JSON,
  `is_dynamic`             BIT(1)        DEFAULT FALSE,
  `imported_id`            BIGINT(20) DEFAULT NULL,
  `copied_from`            BIGINT(20)    DEFAULT NULL,
  `screen_name_id`         BIGINT(20) NOT NULL,
  `is_duplicated`          BIT(1)        DEFAULT 0,
  `created_date`           DATETIME      DEFAULT CURRENT_TIMESTAMP,
  `updated_date`           DATETIME      DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_elements_on_workspace_version_id_and_field_name` (`workspace_version_id`, `element_name`),
  KEY `index_elements_on_workspace_version_id` (`workspace_version_id`),
  CONSTRAINT `fk_workspace_version_id_in_elements_to_workspace_versions` FOREIGN KEY (`workspace_version_id`) REFERENCES `workspace_versions` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `fk_screen_name_id_in_elements_to_element_screen_names` FOREIGN KEY (`screen_name_id`) REFERENCES `element_screen_names` (`id`) ON DELETE RESTRICT ON UPDATE NO ACTION
) ENGINE = InnoDB
  AUTO_INCREMENT = 5
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
