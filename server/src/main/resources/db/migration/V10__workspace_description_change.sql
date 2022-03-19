update workspaces w set description = 'Testsigma Simply travel is demo site. Here we will be showing how to achieve responsive web testing with Testsigma.' where id = 29;
update workspaces w set description = 'Demos how Testsigma simplifies iOS Native Workspace automation.' where id = 31;


ALTER TABLE agents MODIFY COLUMN title varchar(255) COLLATE utf8_unicode_ci NOT NULL;
ALTER TABLE agents ADD CONSTRAINT index_agents_on_title UNIQUE KEY (title);