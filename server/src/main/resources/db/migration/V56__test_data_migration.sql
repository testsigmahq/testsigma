UPDATE `testsigma_opensource`.`test_steps` SET `test_data` = CONCAT("{\"test-data\":{\"test-data\":{\"value\":\"", `test_steps`.`test_data`, "\",\"type\":\"", `test_steps`.`test_data_type`, "\"}}}");
