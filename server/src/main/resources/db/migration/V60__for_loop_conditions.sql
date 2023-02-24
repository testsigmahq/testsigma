DROP TABLE IF EXISTS `for_step_conditions`;

CREATE TABLE `for_step_conditions` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
    `updated_date` datetime DEFAULT CURRENT_TIMESTAMP,
    `test_step_id` bigint(20) NOT NULL,
    `test_data_profile_id` bigint(20) DEFAULT NULL,
    `test_case_id` bigint(20) NOT NULL,
    `iteration_type` varchar(250) COLLATE utf8_unicode_ci NOT NULL,
    `left_param_type` varchar(250) COLLATE utf8_unicode_ci DEFAULT NULL,
    `left_param_value` TEXT DEFAULT NULL,
    `operator` varchar(250) COLLATE utf8_unicode_ci DEFAULT NULL,
    `right_param_type` varchar(250) COLLATE utf8_unicode_ci NOT NULL,
    `right_param_value` TEXT DEFAULT NULL,
    `left_function_id` bigint(20) DEFAULT NULL,
    `right_function_id` bigint(20) DEFAULT NULL,
    `test_data` TEXT,
    `left_data_map` json,
    `right_data_map` json,
    `copied_from` bigint(20) DEFAULT NULL,
    `imported_id` bigint(20) DEFAULT NULL,
    `copied_overriding_from` bigint(20) default null,
    PRIMARY KEY (`id`),
    UNIQUE KEY `for_step_conditions_fk_id` (`id`),
    CONSTRAINT `for_step_conditions_fk_test_step_id` FOREIGN KEY (`test_step_id`) REFERENCES `test_steps` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `for_step_test_case_id` FOREIGN KEY (`test_case_id`) REFERENCES `test_cases` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `for_step_conditions_fk_test_data_id` FOREIGN KEY (`test_data_profile_id`) REFERENCES `test_data` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
