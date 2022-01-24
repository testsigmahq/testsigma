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
import com.testsigma.model.TestSuiteResult;
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
public interface TestSuiteResultRepository extends JpaRepository<TestSuiteResult, Long> {

  Page<TestSuiteResult> findAll(Specification<TestSuiteResult> spec, Pageable pageable);

  List<TestSuiteResult> findAllByEnvironmentResultId(Long environmentResultId);

  List<TestSuiteResult> findAllByEnvironmentResultIdAndResultIsNot(Long environmentResultId, ResultConstant result);

  List<TestSuiteResult> findByEnvironmentResultIdAndStatusOrderByPositionAsc(Long environmentResultId, StatusConstant status);

  List<TestSuiteResult> findAllByEnvironmentResultIdAndIsVisuallyPassed(Long environmentResultId, boolean visualResult);

  List<TestSuiteResult> findAllByEnvironmentResultIdAndIsVisuallyPassedIsNull(Long environmentResultId);

  TestSuiteResult findByEnvironmentResultIdAndSuiteId(Long environmentResultId, Long suiteId);

  Integer countAllByEnvironmentResultIdAndStatusIsNot(Long environmentResultId, StatusConstant status);

  @Query("SELECT " +
    "MAX(" +
    " CASE " +
    "   WHEN suiteResult.result = com.testsigma.model.ResultConstant.SUCCESS THEN 0 " +
    "   WHEN suiteResult.result = com.testsigma.model.ResultConstant.FAILURE THEN 1 " +
    "   WHEN suiteResult.result = com.testsigma.model.ResultConstant.ABORTED THEN 2 " +
    "   WHEN suiteResult.result = com.testsigma.model.ResultConstant.NOT_EXECUTED THEN 3 " +
    "   WHEN suiteResult.result = com.testsigma.model.ResultConstant.QUEUED THEN 4 " +
    "   ELSE 5 " +
    " END) FROM  TestSuiteResult suiteResult " +
    "where suiteResult.environmentResultId =:environmentResultId")
  ResultConstant findMaxResultByEnvironmentResultId(@Param("environmentResultId") Long environmentResultId);

  @Modifying
  @Query("UPDATE TestSuiteResult tcgr SET tcgr.result = :result, tcgr.message = :message, " +
    "tcgr.status = :status, tcgr.duration = :duration, tcgr.startTime = :startTime, tcgr.endTime = :endTime " +
    "WHERE tcgr.environmentResultId = :environmentResultId and tcgr.status = :statusConstant ")
  void updateTestSuiteResult(@Param("result") ResultConstant result, @Param("message") String message,
                             @Param("status") StatusConstant status, @Param("duration") Long duration,
                             @Param("startTime") Timestamp startTime, @Param("endTime") Timestamp endTime,
                             @Param("environmentResultId") Long environmentResultId,
                             @Param("statusConstant") StatusConstant statusConstant);

  @Modifying
  @Query("UPDATE TestSuiteResult tcgr SET tcgr.result = :result, tcgr.message = :message, " +
    "tcgr.status = :status, tcgr.duration = :duration, tcgr.startTime = :startTime, tcgr.endTime = :endTime " +
    "WHERE tcgr.environmentResultId = :environmentResultId and tcgr.status NOT IN (:notInStatus)")
  void stopIncompleteTestSuiteResults(@Param("result") ResultConstant result, @Param("message") String message,
                                      @Param("status") StatusConstant status, @Param("duration") Long duration,
                                      @Param("startTime") Timestamp startTime, @Param("endTime") Timestamp endTime,
                                      @Param("environmentResultId") Long environmentResultId,
                                      @Param("notInStatus") StatusConstant notInStatus);


  @Modifying
  @Query("UPDATE TestSuiteResult tsr SET tsr.result = :result, tsr.message = :message," +
    "tsr.status = com.testsigma.model.StatusConstant.STATUS_COMPLETED, " +
    "tsr.startTime=:startTime, tsr.endTime=:endTime, tsr.duration=:duration " +
    "WHERE tsr.environmentResultId = :environmentResultId " +
    "and tsr.result= com.testsigma.model.ResultConstant.QUEUED")
  void updateResultForStopped(@Param("result") ResultConstant result,
                              @Param("message") String message,
                              @Param("startTime") Timestamp startTime,
                              @Param("endTime") Timestamp endTime,
                              @Param("duration") Long duration,
                              @Param("environmentResultId") Long environmentResultId);


  @Query(value = "UPDATE test_suite_results as tcr " +
    "INNER JOIN (SELECT suite_result_id, COUNT(id) totalCount FROM test_case_results where iteration is null " +
    "AND suite_result_id=:id group by suite_result_id) as tsr " +
    "ON tsr.suite_result_id = tcr.id " +
    "SET tcr.total_count = totalCount where tcr.id = :id", nativeQuery = true)
  @Modifying
  void updateTotalTestCaseResultsCount(@Param("id") Long id);

  @Query(value = "UPDATE test_suite_results as tcr " +
    "LEFT JOIN (SELECT suite_result_id, COUNT(id) totalCount FROM test_case_results where result='SUCCESS' and iteration is null " +
    "AND suite_result_id=:id group by suite_result_id) as tsr " +
    "ON tsr.suite_result_id = tcr.id " +
    "SET tcr.passed_count = COALESCE(totalCount, 0) where tcr.id = :id", nativeQuery = true)
  @Modifying
  void updatePassedTestCaseResultsCount(@Param("id") Long id);

  @Query(value = "UPDATE test_suite_results as tcr " +
    "LEFT JOIN (SELECT suite_result_id, COUNT(id) totalCount FROM test_case_results where result='FAILURE' and iteration " +
    "is null AND suite_result_id=:id group by suite_result_id) as tsr " +
    "ON tsr.suite_result_id = tcr.id " +
    "SET tcr.failed_count = COALESCE(totalCount, 0) where tcr.id = :id", nativeQuery = true)
  @Modifying
  void updateFailedTestCaseResultsCount(@Param("id") Long id);

  @Query(value = "UPDATE test_suite_results as tcr " +
    "LEFT JOIN (SELECT suite_result_id, COUNT(id) totalCount FROM test_case_results where result='ABORTED' and iteration " +
    "is null AND suite_result_id=:id group by suite_result_id) as tsr " +
    "ON tsr.suite_result_id = tcr.id " +
    "SET tcr.aborted_count = COALESCE(totalCount, 0) where tcr.id = :id", nativeQuery = true)
  @Modifying
  void updateAbortedTestCaseResultsCount(@Param("id") Long id);

  @Query(value = "UPDATE test_suite_results as tcr " +
    "LEFT JOIN (SELECT suite_result_id, COUNT(id) totalCount FROM test_case_results where result='NOT_EXECUTED' and iteration " +
    "is null AND suite_result_id=:id group by suite_result_id) as tsr " +
    "ON tsr.suite_result_id = tcr.id " +
    "SET tcr.not_executed_count = COALESCE(totalCount, 0) where tcr.id = :id", nativeQuery = true)
  @Modifying
  void updateNotExecutedTestCaseResultsCount(@Param("id") Long id);

  @Query(value = "UPDATE test_suite_results as tcr " +
    "LEFT JOIN (SELECT suite_result_id, COUNT(id) totalCount FROM test_case_results where result='QUEUED' and iteration " +
    "is null AND suite_result_id=:id group by suite_result_id) as tsr " +
    "ON tsr.suite_result_id = tcr.id " +
    "SET tcr.queued_count = COALESCE(totalCount, 0) where tcr.id = :id", nativeQuery = true)
  @Modifying
  void updateQueuedTestCaseResultsCount(@Param("id") Long id);

  @Query(value = "UPDATE test_suite_results as tcr " +
    "LEFT JOIN (SELECT suite_result_id, COUNT(id) totalCount FROM test_case_results where result='STOPPED' and iteration " +
    "is null AND suite_result_id=:id group by suite_result_id) as tsr " +
    "ON tsr.suite_result_id = tcr.id " +
    "SET tcr.stopped_count = COALESCE(totalCount, 0) where tcr.id = :id", nativeQuery = true)
  @Modifying
  void updateStoppedTestCaseResultsCount(@Param("id") Long id);

  @Modifying
  @Query("UPDATE TestSuiteResult set isVisuallyPassed=:isVisuallyPassed where id=:id")
  void updateVisualResult(@Param("id") Long id, @Param("isVisuallyPassed") boolean isVisuallyPassed);
}
