DROP TABLE IF EXISTS `test_data_sets`;
CREATE TABLE `test_data_sets` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `test_data_id` bigint(20) NOT NULL,
    `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
    `description` mediumtext COLLATE utf8_unicode_ci,
    `expected_to_fail` tinyint(1) DEFAULT '0',
    `data` mediumtext COLLATE utf8_unicode_ci NOT NULL,
    `position` bigint(20) DEFAULT NULL,
    `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
    `updated_date` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `fk_test_data_profile_id_td_sets` (`test_data_id`),
    CONSTRAINT `test_data_sets_ibfk_1` FOREIGN KEY (`test_data_id`) REFERENCES `test_data` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;