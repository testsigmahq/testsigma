DROP TABLE IF EXISTS `provisioning_profile_upload_versions`;
CREATE TABLE `provisioning_profile_upload_versions`
(
  `id`                      BIGINT(20) NOT NULL AUTO_INCREMENT,
  `provisioning_profile_id` BIGINT(20) NOT NULL,
  `upload_version_id`               BIGINT(20) DEFAULT NULL,
  `created_date`            DATETIME   DEFAULT CURRENT_TIMESTAMP,
  `updated_date`            DATETIME   DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_provisioning_profile_upload_versions_upload_version_id` FOREIGN KEY (`upload_version_id`) REFERENCES `upload_versions` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `fk_provisioning_profile_uploads_provisioning_profile_id` FOREIGN KEY (`provisioning_profile_id`) REFERENCES `provisioning_profiles` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB
  CHARSET = utf8
  COLLATE = utf8_unicode_ci;