/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.repository;

import com.testsigma.model.WorkspaceVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface WorkspaceVersionRepository extends JpaSpecificationExecutor<WorkspaceVersion>, PagingAndSortingRepository<WorkspaceVersion, Long>, JpaRepository<WorkspaceVersion, Long> {

  @Modifying
  @Query(value = "insert into test_cases (created_date, start_time, end_time, is_data_driven, is_step_group, priority_id, description, name, status, type, test_data_id, workspace_version_id, pre_requisite, deleted, test_data_start_index, copied_from)  select now(), tcase.start_time, tcase.end_time, tcase.is_data_driven, tcase.is_step_group,tcase.priority_id, tcase.description, tcase.name, tcase.status, tcase.type, tcase.test_data_id, :newVersionId, tcase.pre_requisite, deleted, test_data_start_index, tcase.id from test_cases tcase where tcase.workspace_version_id=:versionId", nativeQuery = true)
  void copyTestCaseDetails(@Param("newVersionId") Long newVersionId, @Param("versionId") Long versionId);

  @Modifying
  @Query(value = "insert into test_steps ( created_date, action, pre_requisite, priority, step_id, test_case_id, step_group_id, natural_text_action_id, type, condition_type, parent_id, wait_time, copied_from, condition_if, test_data, test_data_type, element, attribute, for_loop_start_index, for_loop_end_index, for_loop_test_data_id, test_data_function_id, test_data_function_args ) select now(), step.action, step.pre_requisite, step.priority, step.step_id, tcase.id, step.step_group_id, step.natural_text_action_id, step.type, step.condition_type, step.parent_id, step.wait_time, step.id, step.condition_if, step.test_data, step.test_data_type, step.element, step.attribute, step.for_loop_start_index, step.for_loop_end_index, step.for_loop_test_data_id, step.test_data_function_id, step.test_data_function_args from test_steps step join test_cases tcase on tcase.copied_from = step.test_case_id where tcase.id in ( select id from test_cases where workspace_version_id = :newVersionId )", nativeQuery = true)
  void copyTestStepDetails(@Param("newVersionId") Long newVersionId);

  @Modifying
  @Query(value = "INSERT INTO  rest_step_details(step_id, created_date, url, method, request_headers, payload, status, header_compare_type, response_headers, response_compare_type, response, store_metadata, expected_result_type, header_runtime_data, body_runtime_data, follow_redirects, authorization_type, authorization_value) SELECT step.id, " +
    "now(), rest.url, rest.method, rest.request_headers, rest.payload, rest.status, rest.header_compare_type, rest.response_headers, rest.response_compare_type, rest.response, rest.store_metadata, rest.expected_result_type, rest.header_runtime_data, rest.body_runtime_data, rest.follow_redirects, rest.authorization_type, rest.authorization_value FROM rest_step_details rest JOIN test_steps step ON rest.step_id= step.copied_from WHERE step.test_case_id IN (SELECT id FROM test_cases " +
    "WHERE workspace_version_id = :newVersionId)", nativeQuery = true)
  void copyRestStepDetails(@Param("newVersionId") Long newVersionId);

  @Modifying
  @Query(value = "UPDATE test_steps a JOIN test_steps b ON a.copied_from=b.parent_id SET b.parent_id=a.id WHERE b.test_case_id IN (SELECT id FROM test_cases WHERE workspace_version_id = :newVersionId) and a.test_case_id IN (SELECT id FROM test_cases WHERE workspace_version_id = :newVersionId)", nativeQuery = true)
  void copyConditionalDetails(@Param("newVersionId") Long newVersionId);

  @Modifying
  @Query(value = "INSERT INTO elements( CREATED_DATE, workspace_version_id, locator_value, element_name, element_type, locator_type, metadata, attributes, is_dynamic, create_type, copied_from, screen_name_id) SELECT  now(), :newVersionId, locator_value, element_name, element_type, locator_type, metadata, attributes, is_dynamic, create_type, fd1.id, max(sn.id)  FROM elements as fd1 join element_screen_names  sn on sn.copied_from=fd1.screen_name_id where fd1.workspace_version_id=:versionId group by fd1.id", nativeQuery = true)
  void copyFields(@Param("newVersionId") Long newVersionId, @Param("versionId") Long versionId);

  @Modifying
  @Query(value = "INSERT INTO test_data( CREATED_DATE, version_id, test_data, test_data_name, copied_from)  SELECT now(), " +
    ":newVersionId, test_data,test_data_name, id FROM test_data " +
    "WHERE version_id= :versionId", nativeQuery = true)
  void copyTestData(@Param("newVersionId") Long newVersionId, @Param("versionId") Long versionId);

  @Modifying
  @Query(value = "UPDATE test_cases tcase JOIN test_data data ON tcase.test_data_id = data.copied_from SET tcase.test_data_id=data.id " +
    "WHERE tcase.workspace_version_id = :versionId and data.version_id= :versionId", nativeQuery = true)
  void updateTestDataReference(@Param("versionId") Long versionId);

  @Modifying
  @Query(value = "UPDATE test_steps step JOIN test_cases tcase ON step.step_group_id=tcase.copied_from SET step.step_group_id=tcase.id WHERE step.test_case_id IN (SELECT id FROM test_cases WHERE " +
    "workspace_version_id = :newVersionId ) and tcase.workspace_version_id = :newVersionId", nativeQuery = true)
  void updateStepGroupReference(@Param("newVersionId") Long newVersionId);

  @Modifying
  @Query(value = "UPDATE test_cases a JOIN test_cases b ON a.pre_requisite = b.copied_from SET a.pre_requisite = b.id " +
    "WHERE a.workspace_version_id = :newVersionId and b.workspace_version_id = :newVersionId", nativeQuery = true)
  void updateTestCasePreRequisiteReference(@Param("newVersionId") Long newVersionId);

  @Modifying
  @Query(value = "UPDATE test_steps a JOIN test_steps b ON a.pre_requisite = b.copied_from SET a.pre_requisite=b.id WHERE a.test_case_id IN (SELECT id FROM test_cases WHERE workspace_version_id = :newVersionId) and " +
    " b.test_case_id IN (SELECT id FROM test_cases WHERE workspace_version_id = :newVersionId)", nativeQuery = true)
  void updateStepPreRequirementReference(@Param("newVersionId") Long newVersionId);

  @Modifying
  @Query(value = "insert into test_suites(created_date, workspace_version_id, name, pre_requisite, description, copied_from)  select now(), :newVersionId, name, pre_requisite, description,  id from test_suites where workspace_version_id=:oldVersionId", nativeQuery = true)
  void copyTestSuites(@Param("newVersionId") Long newVersionId, @Param("oldVersionId") Long oldVersionId);

  @Modifying
  @Query(value = "update test_steps step join test_data data on for_loop_test_data_id = data.copied_from set for_loop_test_data_id=data.id where for_loop_test_data_id is not null and data.version_id= :newVersionId and step.test_case_id in (select id from test_cases where workspace_version_id= :newVersionId )", nativeQuery = true)
  void updateStepForLoopTestData(@Param("newVersionId") Long newVersionId);

  @Modifying
  @Query(value = "update test_suites a join test_suites b on a.pre_requisite = b.copied_from set a.pre_requisite=b.id where  a.workspace_version_id = :newVersionId and b.workspace_version_id = :newVersionId", nativeQuery = true)
  void copyTestSuitePrerequisites(@Param("newVersionId") Long newVersionId);

  @Modifying
  @Query(value = "insert into  test_suite_cases(suite_id, test_case_id, position )  select (select id from test_suites where copied_from=suite_id and workspace_version_id=:newVersionId) groupId,  (select id from test_cases where copied_from=grp_map.test_case_id and workspace_version_id=:newVersionId), position  from test_suite_cases grp_map join test_suites grp on grp_map.suite_id=grp.id where grp.workspace_version_id=:oldVersionId", nativeQuery = true)
  void copyGroupTestcaseMappings(@Param("newVersionId") Long newVersionId, @Param("oldVersionId") Long oldVersionId);

  @Modifying
  @Query(value = "insert into test_plans(created_date, workspace_version_id, description, element_time_out, environment_id, name, page_time_out, screenshot, recovery_action, on_aborted_action, re_run_on_failure, on_suite_pre_requisite_failed , on_testcase_pre_requisite_failed , on_step_pre_requisite_failed ,test_lab_type, test_plan_type, match_browser_version, copied_from) select now(), :newVersionId, description, element_time_out, environment_id, name, page_time_out, screenshot, recovery_action,on_aborted_action, re_run_on_failure, on_suite_pre_requisite_failed , on_testcase_pre_requisite_failed, on_step_pre_requisite_failed, test_lab_type, test_plan_type, match_browser_version, id from test_plans where workspace_version_id=:oldVersionId", nativeQuery = true)
  void copyTestPlansFromVersion(@Param("newVersionId") Long newVersionId, @Param("oldVersionId") Long oldVersionId);

  @Modifying
  @Query(value = "insert into test_devices(created_date, title, test_plan_id, device_id, platform_device_id, platform_screen_resolution_id, platform_browser_version_id, platform_os_version_id, disabled, match_browser_version, copied_from) select now(), title, (select id from test_plans where copied_from=test_plan_id and workspace_version_id=?) executionId, device_id, platform_device_id, platform_screen_resolution_id, platform_browser_version_id, platform_os_version_id, disabled, match_browser_version, id from test_devices where test_plan_id in (select id from test_plans where workspace_version_id=?)", nativeQuery = true)
  void copyTestDevices(@Param("newVersionId") Long newVersionId, @Param("oldVersionId") Long oldVersionId);

  @Modifying
  @Query(value = "insert into test_device_suites(test_device_id, suite_id, order_id) select (select id from test_devices env where env.copied_from=test_device_id and test_plan_id in (select id from test_plans where workspace_version_id=:newVersionId))  environmentId,(select id from test_suites where copied_from=suite_id and workspace_version_id=:newVersionId) groupId, order_id from test_device_suites egm join test_devices exenv on egm.test_device_id = exenv.id where exenv.test_plan_id in (select id from test_plans where workspace_version_id=:oldVersionId)", nativeQuery = true)
  void copyTestDeviceGroupMappings(@Param("newVersionId") Long newVersionId, @Param("oldVersionId") Long oldVersionId);

  @Modifying
  @Query(value = "insert into tag_entity_mapping(tag_id, entity_id, type) select (select tu.tag_id from test_suites where copied_from=tu.entity_id and tu.type='TEST_SUITE' and workspace_version_id=:newVersionId) tag_id, (select id from test_suites where copied_from=tu.entity_id and tu.type='TEST_SUITE' and workspace_version_id=:newVersionId) entity_id, (select tu.type from test_suites where copied_from=tu.entity_id and tu.type='TEST_SUITE' and workspace_version_id=:newVersionId) type from tag_entity_mapping tu where (select id from test_suites where copied_from=tu.entity_id and tu.type='TEST_SUITE' and workspace_version_id=:newVersionId) is not null", nativeQuery = true)
  void copyTestSuiteLabels(@Param("newVersionId") Long newVersionId);

  @Modifying
  @Query(value = "insert into tag_entity_mapping(tag_id, entity_id, type) select (select tu.tag_id from test_cases where copied_from=tu.entity_id and tu.type='TEST_CASE' and workspace_version_id=:newVersionId) tag_id, (select id from test_cases where copied_from=tu.entity_id and tu.type='TEST_CASE' and workspace_version_id=:newVersionId) entity_id, (select tu.type from test_cases where copied_from=tu.entity_id and tu.type='TEST_CASE' and workspace_version_id=:newVersionId) type from tag_entity_mapping tu where (select id from test_cases where copied_from=tu.entity_id and tu.type='TEST_CASE' and workspace_version_id=:newVersionId) is not null", nativeQuery = true)
  void copyTestcaseLabels(@Param("newVersionId") Long newVersionId);

  @Modifying
  @Query(value = "insert into tag_entity_mapping(tag_id, entity_id, type) select (select tu.tag_id from elements where copied_from=tu.entity_id and tu.type='ELEMENT' and workspace_version_id=:newVersionId) tag_id, (select id from elements where copied_from=tu.entity_id and tu.type='ELEMENT' and workspace_version_id=:newVersionId) entity_id, (select tu.type from elements where copied_from=tu.entity_id and tu.type='ELEMENT' and workspace_version_id=:newVersionId) type from tag_entity_mapping tu where (select tu.type from elements where copied_from=tu.entity_id and tu.type='ELEMENT' and workspace_version_id=:newVersionId) is not null;", nativeQuery = true)
  void copyElementLabels(@Param("newVersionId") Long newVersionId);

  @Modifying
  @Query(value = "insert into element_screen_names(name, version_id, created_date, updated_date, copied_from) select name, :newVersionId, created_date, updated_date, id from element_screen_names where version_id =:oldVersionId", nativeQuery = true)
  void copyElementScreenNames(@Param("newVersionId") Long newVersionId, @Param("oldVersionId") Long oldVersionId);

  @Modifying
  @Query(value = "insert into tag_entity_mapping(tag_id, entity_id, type) select (select tu.tag_id from test_plans where copied_from=tu.entity_id and tu.type='TEST_PLAN' and workspace_version_id=:newVersionId) tag_id, (select id from test_plans where copied_from=tu.entity_id and tu.type='TEST_PLAN' and workspace_version_id=:newVersionId) entity_id, (select tu.type from test_plans where copied_from=tu.entity_id and tu.type='TEST_PLAN' and workspace_version_id=:newVersionId) type from tag_entity_mapping tu where (select tu.type from test_plans where copied_from=tu.entity_id and tu.type='TEST_PLAN' and workspace_version_id=:newVersionId) is not null", nativeQuery = true)
  void copyTestPlanLabels(@Param("newVersionId") Long newVersionId);

  WorkspaceVersion findFirstByWorkspaceId(@Param("workspaceId") Long workspaceId);
}
