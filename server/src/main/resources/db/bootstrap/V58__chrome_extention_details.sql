DROP TABLE IF EXISTS `chrome_extention_details`;
CREATE TABLE `chrome_extention_details`
(
	`id` int(11) NOT NULL AUTO_INCREMENT,  
	`exclude_attributes` json,
	`exclude_classes` json, 
	`include_classes` json, 
	`include_attriutes` json,
	`chrome_extention_details` json,
	`userdefined_attributes` json DEFAULT NULL, 
	PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;