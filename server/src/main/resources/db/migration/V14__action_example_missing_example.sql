INSERT INTO `natural_text_action_examples` (`id`, `natural_text_action_id`, `description`, `example`, `workspace`, `data`, `created_date`, `updated_date`) VALUES (764,10198,'While element is visible/not visible on the page','In this example, We check element is visible/not visible using While Loop','https://app.testsigma.com/','{\"element\": {\"type\": \" \", \"value\": \" \"}, \"test data\": \" \"}','2021-12-23 17:34:05','2021-12-23 17:34:05');
UPDATE `natural_text_action_examples` SET `example` ='In this example, We check element is enabled/disable using While Loop' WHERE id = 765;


UPDATE `workspaces` SET `description` = 'Add, Delete or Update multiple workspace versions using Live.' WHERE id IN (21,22,23,24);