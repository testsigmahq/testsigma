CREATE TABLE `external_import_nlp_mappings` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `workspace_type` varchar(100) NOT NULL,
    `testsigma_nlp_id` int(11) NOT NULL,
    `external_nlp_id` varchar(255) NOT NULL,
    `external_import_type` varchar(100) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=103 DEFAULT CHARSET=utf8;