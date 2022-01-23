/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.repository;

import com.testsigma.model.ResultConstant;
import com.testsigma.model.StatusConstant;
import com.testsigma.model.TestCaseResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Repository
@Transactional
public interface TestCaseResultRepository extends JpaRepository<TestCaseResult, Long> {

  Page<TestCaseResult> findAll(Specification<TestCaseResult> spec, Pageable pageable);

  List<TestCaseResult> findAllBySuiteResultId(Long suiteResultId);

  List<TestCaseResult> findAllByEnvironmentResultId(Long environmentResultId);

  List<TestCaseResult> findAllBySuiteResultIdAndIsVisuallyPassedIsNull(Long id);

  List<TestCaseResult> findAllBySuiteResultIdAndIsVisuallyPassed(Long suiteResultId, boolean visualResult);

  List<TestCaseResult> findAllBySuiteResultIdAndTestCaseId(Long suiteResultId, Long preRequisite);

  List<TestCaseResult> findAllByParentIdAndStatus(Long parentId, StatusConstant status);

  List<TestCaseResult> findAllBySuiteResultIdAndResultIsNot(Long suiteResultId, ResultConstant result);

  Integer countAllByParentIdAndStatusIsNot(Long parentId, StatusConstant status);

  @Query("SELECT caseResult FROM  TestCaseResult caseResult " +
    "JOIN caseResult.testDeviceResult as environmenResult " +
    "JOIN caseResult.testSuite as suite " +
    "JOIN environmenResult.testDevice as environment " +
    "WHERE environmenResult.testPlanResultId = :executionResultId AND caseResult.isStepGroup = FALSE AND " +
    "caseResult.iteration IS NULL"
  )
  List<TestCaseResult> findAllByExecutionResultId(@Param("executionResultId") Long executionResultId);

  @Query("SELECT tcr FROM TestCaseResult AS tcr JOIN tcr.testCase AS tc WHERE tcr.parentId is NULL " +
    "AND tcr.suiteResultId = :suiteResultId AND tcr.status = :status ORDER BY tcr.position ASC")
  List<TestCaseResult> findByActiveSuiteTestCaseResults(@Param("suiteResultId") Long suiteResultId,
                                                        @Param("status") StatusConstant status);

  @Query("SELECT min(tcr.startTime) FROM TestCaseResult tcr WHERE tcr.environmentResultId =:environmentResultId and tcr.startTime is not null")
  Timestamp findMinTimeStampByEnvironmentResultId(@Param("environmentResultId") Long environmentResultId);

  List<TestCaseResult> findAllByParentIdAndResult(Long parentId, ResultConstant status);

  @Query(nativeQuery = true, value = "SELECT * FROM test_case_results tcr INNER JOIN test_case tc " +
    "ON tcr.test_case_id = tc.id WHERE tcr.parent_id is NULL AND tcr.suite_id = :suiteResultId " +
    "AND tcr.started = :started ORDER BY tcr.order_id LIMIT 1")
  TestCaseResult findByStartedActiveSuiteTestCaseResults(@Param("suiteResultId") Long suiteResultId,
                                                         @Param("started") Boolean started);

  @Query("SELECT count(caseResult.id) FROM  TestCaseResult caseResult where caseResult.suiteResultId =:suiteResultId " +
    "and caseResult.status NOT IN (:notInStatus)")
  Integer countAllBySuiteResultIdAndStatusIsNot(@Param("suiteResultId") Long testcaseResultId,
                                                @Param("notInStatus") StatusConstant notInStatus);

  @Query("SELECT " +
    "MAX(" +
    " CASE " +
    "   WHEN caseResult.result = com.testsigma.model.ResultConstant.SUCCESS THEN 0 " +
    "   WHEN caseResult.result = com.testsigma.model.ResultConstant.FAILURE THEN 1 " +
    "   WHEN caseResult.result = com.testsigma.model.ResultConstant.ABORTED THEN 2 " +
    "   WHEN caseResult.result = com.testsigma.model.ResultConstant.NOT_EXECUTED THEN 3 " +
    "   WHEN caseResult.result = com.testsigma.model.ResultConstant.QUEUED THEN 4 " +
    "   ELSE 5 " +
    "END) FROM  TestCaseResult caseResult where caseResult.suiteResultId =:suiteResultId")
  ResultConstant findMaximumResultBySuiteId(@Param("suiteResultId") Long suiteResultId);

  @Query("SELECT " +
    " MAX(" +
    " CASE " +
    "   WHEN caseResult.result = com.testsigma.model.ResultConstant.SUCCESS THEN 0 " +
    "   WHEN caseResult.result = com.testsigma.model.ResultConstant.FAILURE THEN 1 " +
    "   WHEN caseResult.result = com.testsigma.model.ResultConstant.ABORTED THEN 2 " +
    "   WHEN caseResult.result = com.testsigma.model.ResultConstant.NOT_EXECUTED THEN 3 " +
    "   WHEN caseResult.result = com.testsigma.model.ResultConstant.QUEUED THEN 4 " +
    "   ELSE 5 " +
    " END) FROM  TestCaseResult caseResult where caseResult.parentId =:parentId")
  ResultConstant findMaximumResultByParentId(@Param("parentId") Long parentId);

  @Query("SELECT MAX(caseResult.endTime) FROM  TestCaseResult caseResult where caseResult.parentId =:parentId")
  Timestamp findMaximumEndTimeByParentId(@Param("parentId") Long parentId);

  @Query("SELECT min(caseResult.startTime) FROM  TestCaseResult caseResult where caseResult.parentId =:parentId")
  Timestamp findMinimumStartTimeByParentId(@Param("parentId") Long parentId);

  @Modifying
  @Query("UPDATE TestCaseResult set isVisuallyPassed=:isVisuallyPassed where id=:id")
  void updateVisualResult(@Param("id") Long id, @Param("isVisuallyPassed") boolean isVisuallyPassed);

  @Modifying
  @Query("UPDATE TestCaseResult tcr SET tcr.result = :result, tcr.status = :status,  tcr.message = :message, " +
    "tcr.duration = :duration, tcr.startTime = :startTime, tcr.endTime = :endTime " +
    "WHERE tcr.environmentResultId = :environmentResultId and tcr.status =:statusConstant ")
  void updateTestCaseResultByEnvironmentId(@Param("result") ResultConstant result,
                                           @Param("status") StatusConstant status, @Param("message") String message,
                                           @Param("duration") Long duration, @Param("startTime") Timestamp startTime,
                                           @Param("endTime") Timestamp endTime,
                                           @Param("environmentResultId") Long environmentResultId,
                                           @Param("statusConstant") StatusConstant statusConstant);

  @Modifying
  @Query("UPDATE TestCaseResult tcr SET tcr.result = :result, tcr.status = :status,  tcr.message = :message, " +
    "tcr.duration = :duration, tcr.startTime = :startTime, tcr.endTime = :endTime " +
    "WHERE tcr.environmentResultId = :environmentResultId and tcr.status NOT IN (:notInStatus) ")
  void stopIncompleteTestCaseResults(@Param("result") ResultConstant result, @Param("status") StatusConstant status,
                                     @Param("message") String message, @Param("duration") Long duration,
                                     @Param("startTime") Timestamp startTime, @Param("endTime") Timestamp endTime,
                                     @Param("environmentResultId") Long environmentResultId,
                                     @Param("notInStatus") StatusConstant notInStatus);

  @Modifying
  @Query("UPDATE TestCaseResult tcr SET tcr.result = :result, tcr.status = :status,  tcr.message = :message, " +
    "tcr.duration = :duration, tcr.startTime = :startTime, tcr.endTime = :endTime " +
    "WHERE tcr.suiteResultId = :testSuiteResultId and tcr.status =:statusConstant ")
  void updateTestCaseResultBySuiteResultId(@Param("result") ResultConstant result, @Param("status") StatusConstant status,
                                           @Param("message") String message, @Param("duration") Long duration,
                                           @Param("startTime") Timestamp startTime, @Param("endTime") Timestamp endTime,
                                           @Param("testSuiteResultId") Long testSuiteResultId,
                                           @Param("statusConstant") StatusConstant statusConstant);

  @Modifying
  @Query("UPDATE TestCaseResult tcr SET tcr.result = :result, tcr.message = :message, " +
    "tcr.status = com.testsigma.model.StatusConstant.STATUS_COMPLETED, " +
    "tcr.startTime=:startTime, tcr.endTime=:endTime, tcr.duration=:duration " +
    "WHERE tcr.environmentResultId = :environmentResultId " +
    "and tcr.result= com.testsigma.model.ResultConstant.QUEUED")
  void updateResultForStopped(@Param("result") ResultConstant result,
                              @Param("message") String message,
                              @Param("startTime") Timestamp startTime,
                              @Param("endTime") Timestamp endTime,
                              @Param("duration") Long duration,
                              @Param("environmentResultId") Long environmentResultId);

  @Modifying
  @Query("UPDATE TestCaseResult tcr SET tcr.result = :result, tcr.message = :message, " +
    "tcr.status = com.testsigma.model.StatusConstant.STATUS_COMPLETED, " +
    "tcr.startTime=:startTime, tcr.endTime=:endTime, tcr.duration=:duration " +
    "WHERE tcr.suiteResultId = :testSuiteResultId " +
    "and tcr.result= com.testsigma.model.ResultConstant.QUEUED")
  void updateResultForStoppedByTestSuiteResultId(@Param("result") ResultConstant result,
                                                 @Param("message") String message,
                                                 @Param("startTime") Timestamp startTime,
                                                 @Param("endTime") Timestamp endTime,
                                                 @Param("duration") Long duration,
                                                 @Param("testSuiteResultId") Long testSuiteResultId);

  @Modifying
  @Query(value = "UPDATE test_case_results SET " +
    "start_time = DATE_SUB( DATE_ADD( start_time, INTERVAL DATEDIFF(NOW(), start_time) DAY ), INTERVAL 3 HOUR ), " +
    "end_time=DATE_SUB( DATE_ADD( end_time, INTERVAL DATEDIFF( NOW(), end_time ) DAY), INTERVAL 3 HOUR)",
    nativeQuery = true)
  void updateBootstrapData();

  @Modifying
  @Query(value = "UPDATE test_case_results AS tcr INNER JOIN (SELECT test_case_result_id, COUNT(id) totalCount " +
    "FROM test_step_results WHERE test_case_result_id = :id " +
    "AND (JSON_EXTRACT(test_step_details,'$.condition_type') = 0 " +
    "OR JSON_EXTRACT(test_step_details,'$.condition_type') is NULL) " +
    "AND step_group_result_id is NULL " +
    "GROUP BY test_case_result_id) AS tsr ON tsr.test_case_result_id = tcr.id " +
    "SET tcr.total_count = totalCount WHERE tcr.id = :id", nativeQuery = true)
  void updateTotalTestCaseResultsCount(@Param("id") Long id);

  @Modifying
  @Query(value = "UPDATE test_case_results AS tcr " +
    "LEFT JOIN (SELECT test_case_result_id, COUNT(id) totalCount FROM test_step_results where result='SUCCESS' " +
    "AND test_case_result_id = :id and (JSON_EXTRACT(test_step_details,'$.condition_type') = 0 " +
    "OR JSON_EXTRACT(test_step_details,'$.condition_type') is NULL) " +
    "AND step_group_result_id is NULL " +
    "GROUP BY test_case_result_id) AS tsr " +
    "ON tsr.test_case_result_id = tcr.id " +
    "SET tcr.passed_count = COALESCE(totalCount, 0) WHERE tcr.id = :id", nativeQuery = true)
  void updatePassedTestCaseResultsCount(@Param("id") Long id);

  @Modifying
  @Query(value = "UPDATE test_case_results AS tcr " +
    "LEFT JOIN (SELECT test_case_result_id, COUNT(id) totalCount FROM test_step_results where result='FAILURE' " +
    "AND test_case_result_id = :id and (JSON_EXTRACT(test_step_details,'$.condition_type') = 0 " +
    "OR JSON_EXTRACT(test_step_details,'$.condition_type') is NULL " +
    "AND step_group_result_id is NULL) " +
    "GROUP BY test_case_result_id) AS tsr " +
    "ON tsr.test_case_result_id = tcr.id " +
    "SET tcr.failed_count = COALESCE(totalCount, 0) WHERE tcr.id = :id", nativeQuery = true)
  void updateFailedTestCaseResultsCount(@Param("id") Long id);

  @Modifying
  @Query(value = "UPDATE test_case_results AS tcr " +
    "LEFT JOIN (SELECT test_case_result_id, COUNT(id) totalCount FROM test_step_results where result='ABORTED' " +
    "AND test_case_result_id = :id and (JSON_EXTRACT(test_step_details,'$.condition_type') = 0 " +
    "OR JSON_EXTRACT(test_step_details,'$.condition_type') is NULL " +
    "AND step_group_result_id is NULL ) " +
    "GROUP BY test_case_result_id) AS tsr " +
    "ON tsr.test_case_result_id = tcr.id " +
    "SET tcr.aborted_count = COALESCE(totalCount, 0) WHERE tcr.id = :id", nativeQuery = true)
  void updateAbortedTestCaseResultsCount(@Param("id") Long id);

  @Modifying
  @Query(value = "UPDATE test_case_results AS tcr " +
    "LEFT JOIN (SELECT test_case_result_id, COUNT(id) totalCount FROM test_step_results where result='NOT_EXECUTED' " +
    "AND test_case_result_id = :id and (JSON_EXTRACT(test_step_details,'$.condition_type') = 0 " +
    "OR JSON_EXTRACT(test_step_details,'$.condition_type') is NULL " +
    "AND step_group_result_id is NULL ) " +
    "GROUP BY test_case_result_id) AS tsr " +
    "ON tsr.test_case_result_id = tcr.id " +
    "SET tcr.not_executed_count = COALESCE(totalCount, 0) WHERE tcr.id = :id", nativeQuery = true)
  void updateNotExecutedTestCaseResultsCount(@Param("id") Long id);

  @Modifying
  @Query(value = "UPDATE test_case_results AS tcr " +
    "LEFT JOIN (SELECT test_case_result_id, COUNT(id) totalCount FROM test_step_results where result='QUEUED' " +
    "AND test_case_result_id = :id and (JSON_EXTRACT(test_step_details,'$.condition_type') = 0 " +
    "OR JSON_EXTRACT(test_step_details,'$.condition_type') is NULL " +
    "AND step_group_result_id is NULL ) " +
    "GROUP BY test_case_result_id) AS tsr " +
    "ON tsr.test_case_result_id = tcr.id " +
    "SET tcr.queued_count = COALESCE(totalCount, 0) WHERE tcr.id = :id", nativeQuery = true)
  void updateQueuedTestCaseResultsCount(@Param("id") Long id);

  @Modifying
  @Query(value = "UPDATE test_case_results AS tcr " +
    "LEFT JOIN (SELECT test_case_result_id, COUNT(id) totalCount FROM test_step_results where result='STOPPED' " +
    "AND test_case_result_id = :id and (JSON_EXTRACT(test_step_details,'$.condition_type') = 0 " +
    "OR JSON_EXTRACT(test_step_details,'$.condition_type') is NULL " +
    "AND step_group_result_id is NULL ) " +
    "GROUP BY test_case_result_id) AS tsr " +
    "ON tsr.test_case_result_id = tcr.id " +
    "SET tcr.stopped_count = COALESCE(totalCount, 0) WHERE tcr.id = :id", nativeQuery = true)
  void updateStoppedTestCaseResultsCount(@Param("id") Long id);

  @Modifying
  @Query(value = "UPDATE test_case_results AS tcr " +
    "LEFT JOIN (SELECT parent_id, COUNT(id) totalCount FROM test_case_results WHERE parent_id = :id " +
    "GROUP BY parent_id) AS tsr ON tsr.parent_id = tcr.id " +
    "SET tcr.total_count = COALESCE(totalCount, 0) WHERE tcr.id = :id", nativeQuery = true)
  void updateIterationTotalTestCaseResultsCount(@Param("id") Long id);

  @Modifying
  @Query(value = "UPDATE test_case_results AS tcr " +
    "LEFT JOIN (SELECT parent_id, COUNT(id) passedCount FROM test_case_results where result='SUCCESS' " +
    "AND parent_id = :id GROUP BY parent_id) AS tsr " +
    "ON tsr.parent_id = tcr.id SET tcr.passed_count = COALESCE(passedCount, 0) WHERE tcr.id = :id", nativeQuery = true)
  void updateIterationPassedTestCaseResultsCount(@Param("id") Long id);

  @Modifying
  @Query(value = "UPDATE test_case_results AS tcr " +
    "LEFT JOIN (SELECT parent_id, COUNT(id) failedCount FROM test_case_results where result='FAILURE' " +
    "AND parent_id = :id GROUP BY parent_id) AS tsr ON tsr.parent_id = tcr.id " +
    "SET tcr.failed_count = COALESCE(failedCount, 0) WHERE tcr.id = :id", nativeQuery = true)
  void updateIterationFailedTestCaseResultsCount(@Param("id") Long id);

  @Modifying
  @Query(value = "UPDATE test_case_results AS tcr " +
    "LEFT JOIN (SELECT parent_id, COUNT(id) abortedCount FROM test_case_results where result='ABORTED' " +
    "AND parent_id = :id GROUP BY parent_id) AS tsr ON tsr.parent_id = tcr.id " +
    "SET tcr.aborted_count = COALESCE(abortedCount, 0) WHERE tcr.id = :id", nativeQuery = true)
  void updateIterationAbortedTestCaseResultsCount(@Param("id") Long id);

  @Modifying
  @Query(value = "UPDATE test_case_results AS tcr " +
    "LEFT JOIN (SELECT parent_id, COUNT(id) notExecutedCount FROM test_case_results where result='NOT_EXECUTED' " +
    "AND parent_id = :id GROUP BY parent_id) AS tsr ON tsr.parent_id = tcr.id " +
    "SET tcr.not_executed_count = COALESCE(notExecutedCount, 0) WHERE tcr.id = :id", nativeQuery = true)
  void updateIterationNotExecutedTestCaseResultsCount(@Param("id") Long id);

  @Modifying
  @Query(value = "UPDATE test_case_results AS tcr " +
    "LEFT JOIN (SELECT parent_id, COUNT(id) queuedCount FROM test_case_results where result='QUEUED' " +
    "AND parent_id = :id GROUP BY parent_id) AS tsr ON tsr.parent_id = tcr.id " +
    "SET tcr.queued_count = COALESCE(queuedCount, 0) WHERE tcr.id = :id", nativeQuery = true)
  void updateIterationQueuedTestCaseResultsCount(@Param("id") Long id);

  @Modifying
  @Query(value = "UPDATE test_case_results AS tcr " +
    "LEFT JOIN (SELECT parent_id, COUNT(id) stoppedCount FROM test_case_results where result='STOPPED' " +
    "AND parent_id = :id GROUP BY parent_id) AS tsr ON tsr.parent_id = tcr.id " +
    "SET tcr.stopped_count = COALESCE(stoppedCount, 0) WHERE tcr.id = :id", nativeQuery = true)
  void updateIterationStoppedTestCaseResultsCount(@Param("id") Long id);
}
