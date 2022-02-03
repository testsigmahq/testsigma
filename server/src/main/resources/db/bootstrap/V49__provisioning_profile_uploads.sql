DROP TABLE IF EXISTS `provisioning_profile_uploads`;
CREATE TABLE `provisioning_profile_uploads`
(
  `id`                      BIGINT(20) NOT NULL AUTO_INCREMENT,
  `provisioning_profile_id` BIGINT(20) NOT NULL,
  `upload_id`               BIGINT(20) DEFAULT NULL,
  `created_date`            DATETIME   DEFAULT CURRENT_TIMESTAMP,
  `updated_date`            DATETIME   DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_upload_id_in_provisioning_profile_uploads_to_uploads` FOREIGN KEY (`upload_id`) REFERENCES `uploads` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `fk_provisioning_profile_id_in_profile_uploads_to_profiles` FOREIGN KEY (`provisioning_profile_id`) REFERENCES `provisioning_profiles` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB
  CHARSET = utf8
  COLLATE = utf8_unicode_ci;
