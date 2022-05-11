ALTER TABLE `backup_details` 
ADD COLUMN `skip_entity_exists` BIT(1) DEFAULT NULL,
ADD COLUMN `action_type` VARCHAR (1000) DEFAULT NULL;