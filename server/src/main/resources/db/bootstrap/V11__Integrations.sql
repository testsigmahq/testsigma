DROP TABLE IF EXISTS `integrations`;
CREATE TABLE `integrations`
(
  `id`                     BIGINT(20)                            NOT NULL AUTO_INCREMENT,
  `name`                   VARCHAR(255) COLLATE utf8_unicode_ci  NOT NULL,
  `description`            TEXT COLLATE utf8_unicode_ci,
  `workspace_id`           BIGINT(20)                            NOT NULL,
  `url`                    VARCHAR(1024) COLLATE utf8_unicode_ci NOT NULL,
  `username`               VARCHAR(255) COLLATE utf8_unicode_ci  NOT NULL,
  `password`               VARCHAR(255) COLLATE utf8_unicode_ci  NOT NULL,
  `auth_type`              VARCHAR(255) COLLATE utf8_unicode_ci       DEFAULT NULL,
  `token`                  VARCHAR(1024) COLLATE utf8_unicode_ci      DEFAULT NULL,
  `metadata`               JSON                                       DEFAULT NULL,
  `workspace`            VARCHAR(500) COLLATE utf8_unicode_ci       DEFAULT NULL,
  `refresh_key_expires_at` TIMESTAMP                             NULL DEFAULT NULL,
  `access_key_type`        VARCHAR(500) COLLATE utf8_unicode_ci       DEFAULT NULL,
  `access_key`             TEXT COLLATE utf8_unicode_ci,
  `access_key_issued_at`   TIMESTAMP                             NULL DEFAULT NULL,
  `access_key_expires_at`  TIMESTAMP                             NULL DEFAULT NULL,
  `refresh_key`            TEXT COLLATE utf8_unicode_ci,
  `refresh_key_issued_at`  TIMESTAMP                             NULL DEFAULT NULL,
  `created_date`           DATETIME                                   DEFAULT CURRENT_TIMESTAMP,
  `updated_date`           DATETIME                                   DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `index_integrations_on_workspace` (`workspace`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;
