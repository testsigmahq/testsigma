ALTER TABLE `test_plans`
    ADD FOREIGN KEY `last_run_id_on_test_plan` (`last_run_id`) REFERENCES `test_plan_results` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE `test_cases`
     ADD FOREIGN KEY `last_run_id_on_test_case` (`last_run_id`) REFERENCES `test_case_results` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE `test_steps`
     ADD FOREIGN KEY (`step_group_id`) REFERENCES test_cases (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;
