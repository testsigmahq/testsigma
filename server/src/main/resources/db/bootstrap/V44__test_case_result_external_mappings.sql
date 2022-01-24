DROP TABLE IF EXISTS test_case_result_external_mappings;
CREATE TABLE test_case_result_external_mappings
(
  id                  BIGINT(20)   NOT NULL AUTO_INCREMENT,
  test_case_result_id BIGINT(20)   NOT NULL,
  workspace_id        BIGINT(20)   NOT NULL,
  external_id         VARCHAR(255) NOT NULL,
  misc                TEXT         DEFAULT NULL,
  `created_date`      DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `updated_date`      DATETIME     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY `index_test_case_result_id_and_workspace_id` (test_case_result_id, workspace_id),
  CONSTRAINT `fk_workspace_id_to_integrations` FOREIGN KEY (workspace_id) REFERENCES integrations (id) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `fk_test_case_result_id_in_external_mappings_to_test_case_results` FOREIGN KEY (test_case_result_id) REFERENCES test_case_results (id) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE utf8_unicode_ci;

