UPDATE `test_devices` SET `agent_id` = NULL WHERE `agent_id` not in (SELECT id from `agents`);

ALTER TABLE `test_devices` ADD KEY `index_test_devices_on_agent_id` (`agent_id`);
ALTER TABLE `test_devices` ADD CONSTRAINT `fk_agent_id_in_test_devices_to_uploads` FOREIGN
    KEY (`agent_id`) REFERENCES `agents` (`id`) ON DELETE RESTRICT ON UPDATE NO ACTION;
