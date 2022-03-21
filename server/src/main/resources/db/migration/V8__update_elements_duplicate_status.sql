ALTER TABLE `elements` ADD is_duplicated BIT(1) DEFAULT 0;

set session  sql_mode='';
SET GLOBAL sql_mode=(SELECT REPLACE(@@sql_mode,'ONLY_FULL_GROUP_BY',''));

CREATE TEMPORARY TABLE duplicate_locators SELECT a.* FROM elements a
JOIN (SELECT * FROM `elements` GROUP BY `locator_value`, workspace_version_id, screen_name_id, locator_type  having count(locator_value) > 1 && workspace_version_id IN (SELECT id FROM `workspace_versions`)) b
ON a.locator_value = b.locator_value AND a.workspace_version_id = b.workspace_version_id AND a.screen_name_id= b.screen_name_id AND a.locator_type = b.locator_type
ORDER BY workspace_version_id, locator_value;

UPDATE `elements` SET is_duplicated = 1 WHERE id IN (SELECT id FROM duplicate_locators);
DROP TEMPORARY TABLE duplicate_locators;