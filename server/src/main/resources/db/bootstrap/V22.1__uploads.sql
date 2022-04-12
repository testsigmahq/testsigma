DROP TABLE IF EXISTS `uploads`;
CREATE TABLE `uploads`
(
    `id`             BIGINT(20)   NOT NULL AUTO_INCREMENT,
    `name`           VARCHAR(256) DEFAULT NULL,
    `workspace_id`   BIGINT(20)   DEFAULT NULL,
    `latest_version_id` BIGINT(20) DEFAULT NULL,
    `created_date`   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `updated_date`   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `index_uploads_on_workspace_id_and_name` (`workspace_id`, `name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



