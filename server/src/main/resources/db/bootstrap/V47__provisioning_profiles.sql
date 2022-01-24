DROP TABLE IF EXISTS `provisioning_profiles`;
CREATE TABLE `provisioning_profiles`
(
    `id`           BIGINT(20)   NOT NULL AUTO_INCREMENT,
    `name`         VARCHAR(255) NOT NULL,
    `team_id`      VARCHAR(255) DEFAULT NULL,
    `status`       VARCHAR(255) NOT NULL,
    `created_date` DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `updated_date` DATETIME     DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  CHARSET = utf8
  COLLATE = utf8_unicode_ci;

