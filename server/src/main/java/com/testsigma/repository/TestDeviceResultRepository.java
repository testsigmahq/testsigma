/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.repository;

import com.testsigma.model.TestPlanLabType;
import com.testsigma.model.ResultConstant;
import com.testsigma.model.StatusConstant;
import com.testsigma.model.TestDeviceResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface TestDeviceResultRepository extends JpaRepository<TestDeviceResult, Long> {


  List<TestDeviceResult> findAllByTestPlanResultIdAndStatusIsNot(Long testPlanResultId, StatusConstant notInStatus);

  List<TestDeviceResult> findAllByTestPlanResultIdAndResultIsNot(Long testPlanResultId, ResultConstant notInResult);

  List<TestDeviceResult> findAllByTestPlanResultId(Long testPlanResultId);

  @Query("SELECT DISTINCT(envResult.id) " +
    "FROM TestDeviceResult envResult " +
    "JOIN envResult.testPlanResult exResult ON exResult.id = envResult.testPlanResultId  " +
    "JOIN exResult.testPlan exe ON exResult.testPlanId = exe.id " +
    "JOIN envResult.testDevice ee ON ee.id = envResult.testDeviceId " +
    "JOIN envResult.testSuiteResults tsr ON tsr.environmentResultId = envResult.id " +
    "AND tsr.status = com.testsigma.model.StatusConstant.STATUS_QUEUED " +
    "WHERE ee.disable=FALSE " +
    "ORDER BY envResult.id")
  List<Long> findAllPendingEnvironments();

  @Query("SELECT max(envr.status) FROM TestDeviceResult envr where envr.testPlanResultId = :testPlanResultId")
  StatusConstant maxStatusByTestPlanResultId(@Param("testPlanResultId") Long testPlanResultId);

  Integer countByTestPlanResultIdAndStatusIsNot(Long testPlanResultId, StatusConstant notInStatus);

  @Query("SELECT " +
    "MAX(" +
    " CASE " +
    "   WHEN envrResult.result = com.testsigma.model.ResultConstant.SUCCESS THEN 0 " +
    "   WHEN envrResult.result = com.testsigma.model.ResultConstant.FAILURE THEN 1 " +
    "   WHEN envrResult.result = com.testsigma.model.ResultConstant.ABORTED THEN 2 " +
    "   WHEN envrResult.result = com.testsigma.model.ResultConstant.NOT_EXECUTED THEN 3 " +
    "   WHEN envrResult.result = com.testsigma.model.ResultConstant.QUEUED THEN 4 " +
    "   ELSE 5 " +
    "END) FROM TestDeviceResult envrResult where envrResult.testPlanResultId = :testPlanResultId")
  ResultConstant maxResultByTestPlanResultId(@Param("testPlanResultId") Long testPlanResultId);

  Page<TestDeviceResult> findAll(Specification<TestDeviceResult> spec, Pageable pageable);

  TestDeviceResult findByTestPlanResultIdAndPrerequisiteTestDevicesId(Long testPlanResultId, Long prerequisiteTestDevicesId);

  TestDeviceResult findFirstByTestDeviceAgentIdAndTestPlanLabTypeAndTestPlanResultResultOrderByIdAsc(Long id, TestPlanLabType type, ResultConstant result);

  @Query(value = "UPDATE test_device_results as tcr " +
    "INNER JOIN (SELECT test_device_result_id, COUNT(id) totalCount FROM test_case_results where iteration is null and test_device_result_id=:id group by test_device_result_id) as tsr" +
    "  ON tsr.test_device_result_id = tcr.id " +
    "SET tcr.total_count = totalCount where tcr.id = :id", nativeQuery = true)
  @Modifying
  void updateTotalTestCaseResultsCount(@Param("id") Long id);

  @Query(value = "UPDATE test_device_results as tcr " +
    "LEFT JOIN (SELECT test_device_result_id, COUNT(id) totalCount FROM test_case_results where result='SUCCESS' and iteration is null and test_device_result_id=:id group by test_device_result_id) as tsr" +
    "  ON tsr.test_device_result_id = tcr.id " +
    "SET tcr.passed_count = COALESCE(totalCount, 0) where tcr.id = :id", nativeQuery = true)
  @Modifying
  void updatePassedTestCaseResultsCount(@Param("id") Long id);

  @Query(value = "UPDATE test_device_results as tcr " +
    "LEFT JOIN (SELECT test_device_result_id, COUNT(id) totalCount FROM test_case_results where result='FAILURE' and iteration is null and test_device_result_id=:id group by test_device_result_id) as tsr" +
    "  ON tsr.test_device_result_id = tcr.id " +
    "SET tcr.failed_count = COALESCE(totalCount, 0) where tcr.id = :id", nativeQuery = true)
  @Modifying
  void updateFailedTestCaseResultsCount(@Param("id") Long id);

  @Query(value = "UPDATE test_device_results as tcr " +
    "LEFT JOIN (SELECT test_device_result_id, COUNT(id) totalCount FROM test_case_results where result='ABORTED' and iteration is null and test_device_result_id=:id group by test_device_result_id) as tsr" +
    "  ON tsr.test_device_result_id = tcr.id " +
    "SET tcr.aborted_count = COALESCE(totalCount, 0) where tcr.id = :id", nativeQuery = true)
  @Modifying
  void updateAbortedTestCaseResultsCount(@Param("id") Long id);

  @Query(value = "UPDATE test_device_results as tcr " +
    "LEFT JOIN (SELECT test_device_result_id, COUNT(id) totalCount FROM test_case_results where result='NOT_EXECUTED' and iteration is null and test_device_result_id=:id group by test_device_result_id) as tsr" +
    "  ON tsr.test_device_result_id = tcr.id " +
    "SET tcr.not_executed_count = COALESCE(totalCount, 0) where tcr.id = :id", nativeQuery = true)
  @Modifying
  void updateNotExecutedTestCaseResultsCount(@Param("id") Long id);

  @Query(value = "UPDATE test_device_results as tcr " +
    "LEFT JOIN (SELECT test_device_result_id, COUNT(id) totalCount FROM test_case_results where result='QUEUED' and iteration is null and test_device_result_id=:id group by test_device_result_id) as tsr" +
    "  ON tsr.test_device_result_id = tcr.id " +
    "SET tcr.queued_count = COALESCE(totalCount, 0) where tcr.id = :id", nativeQuery = true)
  @Modifying
  void updateQueuedTestCaseResultsCount(@Param("id") Long id);

  @Query(value = "UPDATE test_device_results as tcr " +
    "LEFT JOIN (SELECT test_device_result_id, COUNT(id) totalCount FROM test_case_results where result='STOPPED' and iteration is null and test_device_result_id=:id group by test_device_result_id) as tsr" +
    "  ON tsr.test_device_result_id = tcr.id " +
    "SET tcr.stopped_count = COALESCE(totalCount, 0) where tcr.id = :id", nativeQuery = true)
  @Modifying
  void updateStoppedTestCaseResultsCount(@Param("id") Long id);

  List<TestDeviceResult> findAllByTestPlanResultIdAndIsVisuallyPassedIsNull(Long testPlanResultId);

  List<TestDeviceResult> findAllByTestPlanResultIdAndIsVisuallyPassed(Long testPlanResultId, boolean visualResult);

  @Modifying
  @Query("UPDATE TestDeviceResult set isVisuallyPassed=:isVisuallyPassed where id=:id")
  void updateVisualResult(@Param("id") Long id, @Param("isVisuallyPassed") boolean isVisuallyPassed);

  @Modifying
  @Query(value = "UPDATE test_device_results envr set envr.result = (select " +
    "max(tsr.result) from test_suite_results tsr " +
    "WHERE tsr.test_device_result_id = :environmentResultId), envr.end_time=IF(envr.result=6, NULL,now()), " +
    "envr.duration=TIME_TO_SEC(TIMEDIFF(end_time, start_time))," +
    "envr.message=IF(envr.result=0, 'Execution Environment Finished Running', IF(envr.result=6, 'Execution Environment Was Stopped', " +
    "IF(envr.result <= 4, 'Execution failure', 'Execution Environment Is In Progress')))  " +
    "WHERE envr.id=:environmentResultId", nativeQuery = true)
  void updateEnvironmentConsolidateResult(@Param("environmentResultId") Long environmentResultId);
}
