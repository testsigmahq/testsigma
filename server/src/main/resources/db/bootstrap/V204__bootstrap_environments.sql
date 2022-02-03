--
-- Dumping data for table `environments`
--

LOCK TABLES `environments` WRITE;
/*!40000 ALTER TABLE `environments` DISABLE KEYS */;
INSERT INTO `environments` (`id`, `name`, `description`, `parameters`, `passwords`, `created_date`, `updated_date`) VALUES (4,'Dev Environment','','{\"url\":\"http://staging.testsigma.com\"}',NULL,'2020-02-18 16:18:06',CURRENT_TIMESTAMP);
INSERT INTO `environments` (`id`, `name`, `description`, `parameters`, `passwords`, `created_date`, `updated_date`) VALUES (5,'Cloud Environment','','{\"url\":\"https://app.testsigma.com\"}',NULL,'2020-02-18 16:18:44',CURRENT_TIMESTAMP);
INSERT INTO `environments` (`id`, `name`, `description`, `parameters`, `passwords`, `created_date`, `updated_date`) VALUES (6,'Environments','','{\"url\":\"google.com\"}',NULL,'2020-02-18 23:46:46',CURRENT_TIMESTAMP);
/*!40000 ALTER TABLE `environments` ENABLE KEYS */;
UNLOCK TABLES;
