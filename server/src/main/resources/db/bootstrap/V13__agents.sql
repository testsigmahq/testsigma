DROP TABLE IF EXISTS `agents`;
CREATE TABLE `agents`
(
  `id`                    BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `unique_id`             VARCHAR(36) COLLATE utf8_unicode_ci  DEFAULT NULL,
  `agent_version`         VARCHAR(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `browser_list`          TEXT COLLATE utf8_unicode_ci,
  `upgrade_agent_jar`     TINYINT(1)                           DEFAULT '0',
  `upgrade_jre`           TINYINT(1)                           DEFAULT '0',
  `upgrade_android_tools` TINYINT(1)                           DEFAULT '0',
  `upgrade_ios_tools`     TINYINT(1)                           DEFAULT '0',
  `upgrade_appium`        TINYINT(1)                           DEFAULT '0',
  `created_date`          DATETIME                             DEFAULT CURRENT_TIMESTAMP,
  `updated_date`          DATETIME                             DEFAULT CURRENT_TIMESTAMP,
  `host_name`             VARCHAR(255)                         DEFAULT NULL,
  `os_version`            VARCHAR(50)                          DEFAULT NULL,
  `ip_address`            VARCHAR(255)                         DEFAULT NULL,
  `os_type`               VARCHAR(255)                         DEFAULT NULL,
  `type`                  INT(11)                              DEFAULT 4,
  `title`                 VARCHAR(255) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_agents_on_title` (`title`),
  UNIQUE KEY `index_agents_on_unique_id` (`unique_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_unicode_ci;
