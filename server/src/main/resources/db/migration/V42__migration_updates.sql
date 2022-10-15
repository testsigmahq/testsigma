DELIMITER //

DROP PROCEDURE IF EXISTS testsigma_opensource.PrerequisiteColumnInDevices //
CREATE PROCEDURE testsigma_opensource.PrerequisiteColumnInDevices ()
    MODIFIES SQL DATA
BEGIN
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = 'test_devices' AND column_name = 'prerequisite_test_devices_id') THEN
      START TRANSACTION ;
      ALTER TABLE `test_devices` ADD COLUMN prerequisite_test_devices_id bigint(20) DEFAULT NULL;
      COMMIT ;
    END IF;
END //

DROP PROCEDURE IF EXISTS testsigma_opensource.PrerequisiteColumnInDeviceResults //
CREATE PROCEDURE testsigma_opensource.PrerequisiteColumnInDeviceResults ()
    MODIFIES SQL DATA
BEGIN
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = 'test_device_results' AND column_name = 'prerequisite_test_device_result_id') THEN
      START TRANSACTION ;
      ALTER TABLE `test_device_results` ADD COLUMN prerequisite_test_device_result_id bigint(20) DEFAULT NULL;
      COMMIT ;
    END IF;
END //

DROP PROCEDURE IF EXISTS testsigma_opensource.WorkspaceVersionColumnInDevices //
CREATE PROCEDURE testsigma_opensource.WorkspaceVersionColumnInDevices ()
    MODIFIES SQL DATA
BEGIN
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = 'test_devices' AND column_name = 'workspace_version_id') THEN
      START TRANSACTION ;
      ALTER TABLE `test_devices` ADD COLUMN workspace_version_id bigint(20) DEFAULT NULL;
      ALTER TABLE `test_devices` ADD CONSTRAINT `fk_workspace_version_id_in_test_devices_to_workspace_versions` FOREIGN KEY (`workspace_version_id`) REFERENCES `workspace_versions` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;
      UPDATE test_devices env SET `workspace_version_id` = (select workspace_version_id from test_plans where env.test_plan_id=id);
      COMMIT ;
    END IF;
END //

DROP PROCEDURE IF EXISTS testsigma_opensource.WorkspaceVersionColumnInDeviceResults //
CREATE PROCEDURE testsigma_opensource.WorkspaceVersionColumnInDeviceResults ()
    MODIFIES SQL DATA
BEGIN
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = 'test_device_results' AND column_name = 'workspace_version_id') THEN
      START TRANSACTION ;
      ALTER TABLE `test_device_results` ADD COLUMN workspace_version_id bigint(20) DEFAULT NULL;
      ALTER TABLE `test_device_results` ADD CONSTRAINT `fk_workspace_version_id_in_device_results_to_workspace_versions` FOREIGN KEY (`workspace_version_id`) REFERENCES `workspace_versions` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;
      UPDATE test_device_results env_result SET `workspace_version_id` = (select workspace_version_id from test_devices where env_result.test_device_id = id);
      COMMIT ;
    END IF;
END //

DROP PROCEDURE IF EXISTS testsigma_opensource.TestLabTypeColumnInDevices //
CREATE PROCEDURE testsigma_opensource.TestLabTypeColumnInDevices ()
    MODIFIES SQL DATA
BEGIN
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = 'test_devices' AND column_name = 'test_lab_type') THEN
      START TRANSACTION ;
      ALTER TABLE `test_devices` ADD COLUMN test_lab_type varchar(255) DEFAULT NULL;
      UPDATE test_devices env SET `test_lab_type` = (select test_lab_type from test_plans where env.test_plan_id=id);
      COMMIT ;
    END IF;
END //

DROP PROCEDURE IF EXISTS testsigma_opensource.TestLabTypeColumnInDeviceResults //
CREATE PROCEDURE testsigma_opensource.TestLabTypeColumnInDeviceResults ()
    MODIFIES SQL DATA
BEGIN
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = 'test_device_results' AND column_name = 'test_lab_type') THEN
      START TRANSACTION ;
      ALTER TABLE `test_device_results` ADD COLUMN test_lab_type varchar(255) DEFAULT NULL;
      UPDATE test_device_results env_result SET `test_lab_type` = (select test_lab_type from test_devices where env_result.test_device_id = id);
      COMMIT ;
    END IF;
END //

DROP PROCEDURE IF EXISTS testsigma_opensource.PasswordsInTestData //
CREATE PROCEDURE testsigma_opensource.PasswordsInTestData ()
    MODIFIES SQL DATA
BEGIN
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = 'test_data' AND column_name = 'passwords') THEN
      START TRANSACTION ;
      ALTER TABLE `test_data` ADD COLUMN `passwords` json DEFAULT NULL;
      COMMIT ;
    END IF;
END //

DELIMITER ;

CALL testsigma_opensource.PrerequisiteColumnInDevices ();
CALL testsigma_opensource.PrerequisiteColumnInDeviceResults ();
CALL testsigma_opensource.WorkspaceVersionColumnInDevices ();
CALL testsigma_opensource.WorkspaceVersionColumnInDeviceResults ();
CALL testsigma_opensource.TestLabTypeColumnInDevices ();
CALL testsigma_opensource.TestLabTypeColumnInDeviceResults ();
CALL testsigma_opensource.PasswordsInTestData ();