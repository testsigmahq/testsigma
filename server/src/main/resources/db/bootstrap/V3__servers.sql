DROP TABLE IF EXISTS `servers`;
CREATE TABLE `servers`
(
  `id`                   BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `server_uuid`          varchar(255) NOT NULL,
  `consent`              TINYINT(1)   NOT NULL DEFAULT '0',
  `consent_request_done` TINYINT(1)   NOT NULL DEFAULT '0',
  `onboarded`      TINYINT(1)   NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_servers_on_server_uuid` (`server_uuid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
