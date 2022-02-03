DROP TABLE IF EXISTS `tag_entity_mapping`;
CREATE TABLE `tag_entity_mapping`
(
  `id`           BIGINT(20) NOT NULL AUTO_INCREMENT,
  `tag_id`       BIGINT(20)   DEFAULT NULL,
  `entity_id`      BIGINT(20)   DEFAULT NULL,
  `type`         VARCHAR(255) DEFAULT NULL,
  `created_date` DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `updated_date` DATETIME     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_tag_entity_mapping_on_tag_id_and_entity_id_and_type` (`tag_id`, `entity_id`, `type`),
  CONSTRAINT `fk_tag_id_in_tag_entity_mapping_to_tags` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
