DROP TABLE IF EXISTS `provisioning_profile_devices`;
CREATE TABLE `provisioning_profile_devices`
(
  `id`                      BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `provisioning_profile_id` BIGINT(20)   NOT NULL,
  `agent_device_id`         BIGINT(20) DEFAULT NULL,
  `device_udid`             VARCHAR(255) NOT NULL,
  `created_date`            DATETIME   DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (`id`),
  UNIQUE KEY `index_profile_devices_on_device_udid_and_agent_device_id` (`device_udid`, `agent_device_id`),
  KEY `index_provisioning_profile_devices_on_agent_device_id` (`agent_device_id`),
  KEY `index_provisioning_profile_devices_on_provisioning_profile_id` (`provisioning_profile_id`),
  CONSTRAINT `fk_agent_device_id_to_agent_devices` FOREIGN KEY (`agent_device_id`) REFERENCES `agent_devices` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `fk_provisioning_profile_id_to_provisioning_profiles` FOREIGN KEY (`provisioning_profile_id`) REFERENCES `provisioning_profiles` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB
  CHARSET = utf8
  COLLATE = utf8_unicode_ci;

