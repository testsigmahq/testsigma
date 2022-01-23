--
-- Dumping data for table `storage_config`
--

LOCK TABLES `storage_config` WRITE;
/*!40000 ALTER TABLE `storage_config` DISABLE KEYS */;
INSERT INTO `storage_config` (`id`, `storage_type`, `aws_bucket_name`, `aws_region`, `aws_endpoint`, `aws_access_key`, `aws_secret_key`, `azure_blob_connection_string`, `azure_blob_container_name`, `on_premise_root_directory`) VALUES (3,'ON_PREMISE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'file_store');
/*!40000 ALTER TABLE `storage_config` ENABLE KEYS */;
UNLOCK TABLES;
