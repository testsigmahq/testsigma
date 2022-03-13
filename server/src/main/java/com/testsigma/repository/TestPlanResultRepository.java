/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.repository;

import com.testsigma.model.StatusConstant;
import com.testsigma.model.TestPlanResult;
import com.testsigma.model.TestPlanResultAndCount;
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
public interface TestPlanResultRepository extends JpaRepository<TestPlanResult, Long> {

  TestPlanResult findByTestPlanIdAndStatusIsNot(Long testPlanId, StatusConstant status);

  TestPlanResult findByIdAndTestPlanId(Long id, Long testPlanId);

  TestPlanResult findByReRunParentId(Long ReRunParentId);

  Page<TestPlanResult> findAll(Specification<TestPlanResult> spec, Pageable pageable);

  @Query(value = "UPDATE test_plan_results as tcr " +
    "INNER JOIN (SELECT test_plan_result_id, COUNT(id) totalCount FROM test_case_results where iteration is null and test_plan_result_id=:testPlanResultId group by test_plan_result_id) as tsr" +
    "  ON tsr.test_plan_result_id = tcr.id " +
    "SET tcr.total_count = totalCount where tcr.id = :testPlanResultId", nativeQuery = true)
  @Modifying
  void updateTotalTestCaseResultsCount(@Param("testPlanResultId") Long testPlanResultId);

  @Query(value = "UPDATE test_plan_results as tcr " +
    "INNER JOIN (SELECT test_plan_result_id, COUNT(id) passedCount FROM test_case_results where result='SUCCESS' and iteration is null and test_plan_result_id=:testPlanResultId group by test_plan_result_id) as tsr" +
    "  ON tsr.test_plan_result_id = tcr.id " +
    "SET tcr.passed_count = passedCount where tcr.id = :testPlanResultId", nativeQuery = true)
  @Modifying
  void updatePassedTestCaseResultsCount(@Param("testPlanResultId") Long testPlanResultId);

  @Query(value = "UPDATE test_plan_results as tcr " +
    "INNER JOIN (SELECT test_plan_result_id, COUNT(id) failedCount FROM test_case_results where result='FAILURE' and iteration is null and test_plan_result_id=:testPlanResultId group by test_plan_result_id) as tsr" +
    "  ON tsr.test_plan_result_id = tcr.id " +
    "SET tcr.failed_count = failedCount where tcr.id = :testPlanResultId", nativeQuery = true)
  @Modifying
  void updateFailedTestCaseResultsCount(@Param("testPlanResultId") Long testPlanResultId);

  @Query(value = "UPDATE test_plan_results as tcr " +
    "INNER JOIN (SELECT test_plan_result_id, COUNT(id) abortedCount FROM test_case_results where result='ABORTED' and iteration is null and test_plan_result_id=:testPlanResultId group by test_plan_result_id) as tsr" +
    "  ON tsr.test_plan_result_id = tcr.id " +
    "SET tcr.aborted_count = abortedCount where tcr.id = :testPlanResultId", nativeQuery = true)
  @Modifying
  void updateAbortedTestCaseResultsCount(@Param("testPlanResultId") Long testPlanResultId);

  @Query(value = "UPDATE test_plan_results as tcr " +
    "INNER JOIN (SELECT test_plan_result_id, COUNT(id) notExecutedCount FROM test_case_results where result='NOT_EXECUTED' and iteration is null and test_plan_result_id=:testPlanResultId group by test_plan_result_id) as tsr" +
    "  ON tsr.test_plan_result_id = tcr.id " +
    "SET tcr.not_executed_count = notExecutedCount where tcr.id = :testPlanResultId", nativeQuery = true)
  @Modifying
  void updateNotExecutedTestCaseResultsCount(@Param("testPlanResultId") Long testPlanResultId);

  @Query(value = "UPDATE test_plan_results as tcr " +
    "LEFT JOIN (SELECT test_plan_result_id, COUNT(id) queuedCount FROM test_case_results where result='QUEUED' and iteration is null and test_plan_result_id=:testPlanResultId group by test_plan_result_id) as tsr" +
    "  ON tsr.test_plan_result_id = tcr.id " +
    "SET tcr.queued_count = COALESCE(queuedCount, 0) where tcr.id = :testPlanResultId", nativeQuery = true)
  @Modifying
  void updateQueuedTestCaseResultsCount(@Param("testPlanResultId") Long testPlanResultId);

  @Query(value = "UPDATE test_plan_results as tcr " +
    "INNER JOIN (SELECT test_plan_result_id, COUNT(id) stoppedCount FROM test_case_results where result='STOPPED' and iteration is null and test_plan_result_id=:testPlanResultId group by test_plan_result_id) as tsr" +
    "  ON tsr.test_plan_result_id = tcr.id " +
    "SET tcr.stopped_count = COALESCE(stoppedCount, 0) where tcr.id = :testPlanResultId", nativeQuery = true)
  @Modifying
  void updateStoppedTestCaseResultsCount(@Param("testPlanResultId") Long testPlanResultId);

  @Modifying
  @Query("UPDATE TestPlanResult set isVisuallyPassed=:isVisuallyPassed where id=:id")
  void updateVisualResult(@Param("id") Long id, @Param("isVisuallyPassed") boolean isVisuallyPassed);


  @Query(value = "SELECT er FROM TestPlanResult er JOIN er.testDeviceResults envr ON er.id = envr.testPlanResultId " +
          "JOIN envr.testDevice ee ON ee.id = envr.testDeviceId " +
          "JOIN er.testPlan exe ON exe.id = er.testPlanId " +
          "WHERE envr.status IN (:statuses) GROUP BY er.id")
  List<TestPlanResult> countOngoingEnvironmentResultsGroupByTestPlanResult(
          @Param(value = "statuses") List<StatusConstant> statuses);

  @Query("SELECT  er.id AS executionResultId, COUNT(tsr.id) AS resultCount " +
          "FROM TestPlanResult er JOIN er.testDeviceResults AS envr ON er.id = envr.testPlanResultId " +
          "JOIN er.testPlan exe ON exe.id = er.testPlanId " +
          "JOIN envr.testDevice ee ON ee.id = envr.testDeviceId " +
          "JOIN envr.testSuiteResults tsr ON envr.id = tsr.environmentResultId " +
          "WHERE tsr.status IN (:statuses) GROUP BY er.id")
  List<TestPlanResultAndCount> countOngoingParallelTestSuiteResultsGroupByTestPlanResult(
          @Param(value = "statuses") List<StatusConstant> statuses);

  @Query("SELECT  er.id AS testPlanResultId, COUNT(envr.id) AS resultCount " +
          "FROM TestPlanResult er JOIN er.testDeviceResults AS envr ON er.id = envr.testPlanResultId " +
          "JOIN er.testPlan exe ON exe.id = er.testPlanId " +
          "JOIN envr.testDevice ee ON ee.id = envr.testDeviceId " +
          "WHERE envr.status IN (:statuses) GROUP BY er.id")
  List<TestPlanResultAndCount> countOngoingNonParallelEnvironmentResultsGroupByTestPlanResult(
          @Param(value = "statuses") List<StatusConstant> statuses);
}
