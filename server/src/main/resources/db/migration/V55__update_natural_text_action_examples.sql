DROP TABLE IF EXISTS `natural_text_action_examples`;

CREATE TABLE `natural_text_action_examples`
(
    `id`           BIGINT(20) NOT NULL AUTO_INCREMENT,
    `natural_text_action_id`  BIGINT(20) NOT NULL,
    `description`  TEXT DEFAULT NULL,
    `example`      TEXT DEFAULT NULL,
    `workspace`    TEXT DEFAULT NULL,
    `data`         JSON DEFAULT NULL,
    `created_date` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_date` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `index_natural_text_action_examples_on_natural_text_action_id` (`natural_text_action_id`),
    CONSTRAINT `fk_natural_text_action_id_to_natural_text_actions` FOREIGN KEY (natural_text_action_id) REFERENCES natural_text_actions (id) ON DELETE CASCADE
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;

INSERT INTO `natural_text_action_examples` (`id`, `natural_text_action_id`, `description`, `example`, `workspace`, `data`, `created_date`, `updated_date`) VALUES (865, 1106,'Store test-data1 in test-data2','In this example, we can store any data into a runtime variable.', '{}','{}','2023-01-28 16:06:57','2023-01-28 16:06:57');
INSERT INTO `natural_text_action_examples` (`id`, `natural_text_action_id`, `description`, `example`, `workspace`, `data`, `created_date`, `updated_date`) VALUES (866, 10222,'Store test-data1 in test-data2','In this example, we can store any data into a runtime variable.', '{}','{}','2023-01-28 16:06:57','2023-01-28 16:06:57');
INSERT INTO `natural_text_action_examples` (`id`, `natural_text_action_id`, `description`, `example`, `workspace`, `data`, `created_date`, `updated_date`) VALUES (867, 40060,'Store test-data1 in test-data2','In this example, we can store any data into a runtime variable.', '{}','{}','2023-01-28 16:06:57','2023-01-28 16:06:57');
INSERT INTO `natural_text_action_examples` (`id`, `natural_text_action_id`, `description`, `example`, `workspace`, `data`, `created_date`, `updated_date`) VALUES (868, 40061,'Store test-data1 in test-data2','In this example, we can store any data into a runtime variable.', '{}','{}','2023-01-28 16:06:57','2023-01-28 16:06:57');