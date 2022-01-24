DROP TABLE IF EXISTS `agent_devices`;
CREATE TABLE `agent_devices`
(
  `id`            BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `name`          VARCHAR(255) NOT NULL,
  `unique_id`     VARCHAR(255) NOT NULL,
  `agent_id`      BIGINT(20)   NOT NULL,
  `product_model` VARCHAR(255) NOT NULL,
  `os_version`    VARCHAR(10)  NOT NULL,
  `os_name`       VARCHAR(50)  NOT NULL,
  `api_level`     VARCHAR(10) DEFAULT NULL,
  `abi`           VARCHAR(50) DEFAULT NULL,
  `browser_list`  text COLLATE utf8_unicode_ci,
  `is_emulator`   TINYINT(1)  DEFAULT 0,
  `is_online`     TINYINT(1)  DEFAULT 0,
  `screen_width`  INT(5),
  `screen_height` INT(5),
  `created_date`  DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_date`  DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_agent_devices_on_agent_id_and_unique_id` (`agent_id`, `unique_id`),
  CONSTRAINT `fk_agent_id_in_agent_devices_to_agents` FOREIGN KEY (`agent_id`) REFERENCES `agents` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB
  CHARSET = utf8
  COLLATE = utf8_unicode_ci;
