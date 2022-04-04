ALTER TABLE test_cases ADD COLUMN is_active TINYINT(1);

UPDATE test_cases SET is_active=true WHERE deleted=false OR deleted IS NULL;

ALTER TABLE test_cases DROP INDEX index_test_cases_on_name;

alter table test_cases
    ADD UNIQUE INDEX test_cases_unique_name (workspace_version_id, name, is_active);