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
import com.testsigma.model.TestStepResult;
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
import java.util.Optional;

@Repository
@Transactional
public interface TestStepResultRepository extends JpaRepository<TestStepResult, Long> {

  Page<TestStepResult> findAll(Specification<TestStepResult> spec, Pageable pageable);

  Optional<TestStepResult> findFirstByTestCaseResultIdAndStepIdOrderByIdDesc(Long testCaseResultId, Long testCaseStepId);

  List<TestStepResult> findAllByTestCaseResultIdAndScreenshotNameIsNotNull(Long testcaseResultId);

  @Query("SELECT " +
    "MAX(" +
    " CASE " +
    "   WHEN stepResult.result = com.testsigma.model.ResultConstant.SUCCESS THEN 0 " +
    "   WHEN stepResult.result = com.testsigma.model.ResultConstant.FAILURE THEN 1 " +
    "   WHEN stepResult.result = com.testsigma.model.ResultConstant.ABORTED THEN 2 " +
    "   WHEN stepResult.result = com.testsigma.model.ResultConstant.NOT_EXECUTED THEN 3 " +
    "   WHEN stepResult.result = com.testsigma.model.ResultConstant.QUEUED THEN 4 " +
    "   ELSE 5 " +
    " END) FROM  TestStepResult stepResult " +
    "where stepResult.testCaseResultId =:testcaseResultId")
  ResultConstant findMaxResultByTestCaseResultId(@Param("testcaseResultId") Long testcaseResultId);

  @Query("SELECT " +
      "MAX(" +
      " CASE " +
      "   WHEN stepResult.result = com.testsigma.model.ResultConstant.SUCCESS THEN 0 " +
      "   WHEN stepResult.result = com.testsigma.model.ResultConstant.FAILURE THEN 1 " +
      "   WHEN stepResult.result = com.testsigma.model.ResultConstant.ABORTED THEN 2 " +
      "   WHEN stepResult.result = com.testsigma.model.ResultConstant.NOT_EXECUTED THEN 3 " +
      "   WHEN stepResult.result = com.testsigma.model.ResultConstant.QUEUED THEN 4 " +
      "   ELSE 5 " +
      " END) FROM  TestStepResult stepResult " +
    "where stepResult.groupResultId =:groupResultId")
  ResultConstant findMaxResultBygroupResultId(@Param("groupResultId") Long groupResultId);

  @Query("SELECT min(stepResult.startTime) FROM  TestStepResult stepResult " +
    "where stepResult.testCaseResultId =:testcaseResultId")
  Timestamp findMinimumStartTimeByTestCaseResultId(@Param("testcaseResultId") Long testcaseResultId);

  @Query("SELECT MAX(stepResult.endTime) FROM  TestStepResult stepResult " +
    "where stepResult.testCaseResultId =:testcaseResultId")
  Timestamp findMaximumEndTimeByTestCaseResultId(@Param("testcaseResultId") Long testcaseResultId);

  @Query("SELECT min(stepResult.startTime) FROM  TestStepResult stepResult " +
    "where stepResult.groupResultId =:groupResultId")
  Timestamp findMinimumStartTimeBygroupResultId(@Param("groupResultId") Long groupResultId);

  @Query("SELECT MAX(stepResult.endTime) FROM  TestStepResult stepResult " +
    "where stepResult.groupResultId =:groupResultId")
  Timestamp findMaximumEndTimeBygroupResultId(@Param("groupResultId") Long groupResultId);

  @Query("SELECT count(stepResult.id) FROM  TestStepResult stepResult " +
    "where stepResult.groupResultId =:groupResultId and stepResult.result =:result")
  Integer countAllBygroupResultIdAndResult(@Param("groupResultId") Long groupResultId,
                                               @Param("result") ResultConstant result);

  @Query("SELECT count(stepResult.id) FROM  TestStepResult stepResult " +
    "where stepResult.testCaseResultId =:testcaseResultId and stepResult.result =:result")
  Integer countAllByTestCaseResultIdAndResult(@Param("testcaseResultId") Long testcaseResultId,
                                              @Param("result") ResultConstant result);

  @Modifying
  @Query("UPDATE TestStepResult set  result =:result, message =:message, startTime =:startTime, endTime =:endTime, " +
    "duration =:duration WHERE result =:queuedStatus and testCaseResultId =:resultId")
  Integer updateStepResult(@Param("result") ResultConstant result, @Param("message") String message,
                           @Param("startTime") Timestamp startTime, @Param("endTime") Timestamp endTime,
                           @Param("duration") Long duration, @Param("queuedStatus") ResultConstant queuedStatus,
                           @Param("resultId") Long resultId);

  @Modifying
  @Query("UPDATE TestStepResult set  result =:result, message =:message, startTime =:startTime, endTime =:endTime, " +
    "duration =:duration WHERE result =:queuedStatus and groupResultId =:resultId")
  Integer updateStepGroupResult(@Param("result") ResultConstant result, @Param("message") String message,
                                @Param("startTime") Timestamp startTime, @Param("endTime") Timestamp endTime,
                                @Param("duration") Long duration,
                                @Param("queuedStatus") ResultConstant queuedStatus,
                                @Param("resultId") Long resultId);

  //Not using default query methods since it will fire a select query and fire individual delete queries for each record
  @Modifying
  @Query("DELETE FROM TestStepResult tsr WHERE tsr.testCaseResultId = :testCaseResultId AND tsr.envRunId = :environmentResultId")
  Integer deleteByTestCaseResultIdAndEnvironmentResultId(@Param("testCaseResultId") Long testCaseResultId,
                                                         @Param("environmentResultId") Long environmentResultId);

  @Modifying
  @Query("UPDATE TestStepResult tcr SET tcr.result = :result,  tcr.message = :message, " +
    "tcr.duration = :duration, tcr.startTime = :startTime, tcr.endTime = :endTime " +
    "WHERE tcr.envRunId = :environmentResultId and tcr.result IN (:inResult) ")
  void stopIncompleteTestStepResults(@Param("result") ResultConstant result,
                                     @Param("message") String message, @Param("duration") Long duration,
                                     @Param("startTime") Timestamp startTime, @Param("endTime") Timestamp endTime,
                                     @Param("environmentResultId") Long environmentResultId,
                                     @Param("inResult") ResultConstant inResult);

  List<TestStepResult> findAllByTestCaseResultId(Long id);
}
