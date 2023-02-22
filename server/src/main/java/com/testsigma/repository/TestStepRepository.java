/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.repository;

import com.testsigma.model.TestStep;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface TestStepRepository extends JpaRepository<TestStep, Long> {

  Optional<TestStep> findTopByTestCaseIdOrderByPositionDesc(Long testCaseId);

  List<TestStep> findAllByTestCaseIdOrderByPositionAsc(Long testCaseId);

  List<TestStep> findAllByParentId(Long parentId);

  List<TestStep> findAllByTestCaseIdAndDisabledIsNotOrderByPositionAsc(Long testCaseId, Boolean notDisabled);

  List<TestStep> findAllByTestCaseIdAndNaturalTextActionIdIn(Long testCaseId, List<Integer> NaturalTextActionIds);


  List<TestStep> findAllByTestCaseIdInAndAddonActionIdIsNotNull(List<Long> testCaseIds);

  List<TestStep> findAllByParentIdOrderByPositionAsc(Long parentId);

  @EntityGraph(attributePaths = {"parentStep"})
  List<TestStep> findAllByIdInOrderByPositionAsc(Long[] ids);

  @Query(value = "SELECT ts.element AS elementName FROM test_steps AS ts " +
    "where ts.element IS NOT NULL AND ts.test_case_id IN(:testCaseIds)",
    nativeQuery = true)
  List<String> findTestStepsByTestCaseIdIn(@Param("testCaseIds") List<Long> testCaseIds);

  Optional<TestStep> findFirstByTestCaseIdOrderByPositionDesc(@Param("testCaseId") Long testCaseId);

  Page<TestStep> findAll(Specification<TestStep> spec, Pageable pageable);

  @Modifying
  @Query(value = "UPDATE TestStep SET position = position-1 WHERE testCaseId=:testCaseId AND position >= :position")
  void decrementPosition(@Param("position") Integer position, @Param("testCaseId") Long testCaseId);

  @Modifying
  @Query(value = "UPDATE TestStep SET position = position+1 WHERE testCaseId=:testCaseId AND position >= :position")
  void incrementPosition(@Param("position") Integer position, @Param("testCaseId") Long testCaseId);

  @Query(value = "UPDATE TestStep SET priority=COALESCE(:priority, priority), waitTime=COALESCE(:waitTime, waitTime) " +
          ",visualEnabled=COALESCE(:visualEnabled,visualEnabled) " +
    "WHERE id in :ids")
  @Modifying
  void bulkUpdateProperties(@Param("ids") Long[] ids,
                            @Param("priority") String priority,
                            @Param("waitTime") Integer waitTime,
                            @Param("visualEnabled") Boolean visualEnabled);

  List<TestStep> findAllByTestCaseIdAndIdInOrderByPosition(Long testCaseId, List<Long> stepIds);

  @Query(value = "select * from test_steps where (condition_type is null or condition_type=0)  and test_case_id in (select id from test_cases where test_data_id=:testDataId) and parent_id is null", nativeQuery = true)
  List<TestStep> getTopLevelTestDataParameter(@Param("testDataId") Long testDataId);

  @Query(value = "select * from test_steps where for_loop_test_data_id is null and test_data_profile_step_id=:testDataProfileStepId", nativeQuery = true)
  List<TestStep> getChildStepsTestDataParameterUsingTestDataProfileId(@Param("testDataProfileStepId") Long testDataProfileStepId);

  @Query(value = "update  test_steps step inner join test_cases on test_cases.id = step.test_case_id set step.element=:newName where step.element=:oldName and test_cases.workspace_version_id=:workspaceVersionId", nativeQuery = true)
  @Modifying
  void updateElementName(@Param("newName") String newName,
                         @Param("oldName") String oldName, @Param("workspaceVersionId")Long workspaceVersionId);

  @Query(value = "select * from test_steps step inner join test_cases on test_cases.id = step.test_case_id where addon_action_id is not null\n" +
    "and JSON_SEARCH(JSON_EXTRACT(addon_natural_text_action_data , '$.\"elements\".*.*'), 'all', :oldName) is not null and test_cases.workspace_version_id=:workspaceVersionId\n", nativeQuery = true)
  List<TestStep> findAddonElementsByName(@Param("oldName") String oldName, @Param("workspaceVersionId")Long workspaceVersionId);

  @Query(value = "select step.* from test_steps step where step.for_loop_start_index is null and step.parent_id is null and test_case_id in (select id from test_cases where test_data_id=:testDataId) and (condition_type is not null and condition_type > 0)", nativeQuery = true)
  List<TestStep> getTopLevelConditionalStepsExceptLoop(@Param("testDataId") Long testDataId);

  @Query(value = "select * from test_steps where for_loop_start_index is null and parent_id=:parentId", nativeQuery = true)
  List<TestStep> getChildStepsTestDataParameter(@Param("parentId") Long parentId);

  @Query(value = "select step.* from test_steps step where step.for_loop_start_index is null and step.parent_id=:parentId and (condition_type is not null and condition_type > 0)", nativeQuery = true)
  List<TestStep> getChildConditionalStepsExceptLoop(@Param("parentId") Long parentId);

  @Query(value = "select step.* from test_steps step where step.for_loop_test_data_id=:testDataId", nativeQuery = true)
  List<TestStep> getAllLoopSteps(@Param("testDataId") Long testDataId);

  Integer countAllByAddonActionIdIn(List<Long> ids);

  List<TestStep> findAllByTestCaseIdAndDisabledIsNotAndStepGroupIdIsNotNullOrderByPositionAsc(Long testCaseId, boolean notDisabled);

  Optional<TestStep> findByTestCaseIdInAndImportedId(List<Long> id, Long id1);

    List<TestStep> findAllByTestCaseIdInOrderByPositionAsc(List<Long> testCaseIds);
    List<TestStep> findAllByTestCaseIdAndPreRequisiteStepId(Long testCaseId,Long prerequisiteId);
    List<TestStep> findAllByTestCaseIdAndPreRequisiteStepIdNotNull(Long testCaseId);


  Optional<TestStep> findAllByTestCaseIdAndImportedId(Long workspaceVersionId, Long importedId);

  @Query(value = "SELECT testStep from TestStep testStep JOIN TestCase tcase on tcase.id = testStep.testCaseId JOIN RestStep rstep on rstep.stepId = testStep.id  where tcase.workspaceVersionId = :versionId and (rstep.headerRuntimeData IS NOT NULL OR rstep.bodyRuntimeData IS NOT NULL)")
  List<TestStep> getAllRestStepWithRuntime(@Param("versionId") Long versionId);

  @Query(value = "SELECT testStep from TestStep testStep JOIN testStep.testCase AS testCase " +
          "ON  testStep.testCaseId = testCase.id AND testCase.workspaceVersionId = :workspaceVersionId " +
          "WHERE  testStep.naturalTextActionId IN (:naturalTextActionIds)")
  List<TestStep> findAllByWorkspaceVersionIdAndNaturalTextActionId(@Param(value = "workspaceVersionId") Long workspaceVersionId,
                                                                   @Param(value = "naturalTextActionIds") List<Integer> naturalTextActionIds);

  @Modifying
  @Query("DELETE FROM TestStep step WHERE step.testCaseId = :testCaseId")
  void deleteStepsByTestCaseId(@Param("testCaseId") Long testCaseId);


  @Modifying
  @Query("DELETE FROM TestStep step WHERE step.stepGroupId = :stepGroupId")
  void deleteStepsByStepGroupId(@Param("stepGroupId") Long stepGroupId);

}
