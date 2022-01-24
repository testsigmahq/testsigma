DROP TABLE IF EXISTS `natural_text_action_examples`;
CREATE TABLE `natural_text_action_examples`
(
  `id`           BIGINT(20) NOT NULL AUTO_INCREMENT,
  `natural_text_action_id`  BIGINT(20),
  `description`  TEXT,
  `example`      TEXT,
  `workspace`  TEXT,
  `data`         JSON,
  `created_date` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_date` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `index_natural_text_action_examples_on_natural_text_action_id` (`natural_text_action_id`),
  CONSTRAINT `fk_natural_text_action_id_to_natural_text_actions` FOREIGN KEY (natural_text_action_id) REFERENCES natural_text_actions (id) ON DELETE CASCADE
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
