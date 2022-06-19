/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.repository;

import com.testsigma.model.AbstractTestSuite;
import com.testsigma.model.TestSuite;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface TestSuiteRepository extends PagingAndSortingRepository<TestSuite, Long>,
  JpaSpecificationExecutor<TestSuite>, JpaRepository<TestSuite, Long> {


  @Query("SELECT testSuite FROM AbstractTestSuite AS testSuite " +
    "JOIN FETCH TestDeviceSuite AS envMapping " +
    "ON envMapping.testDeviceId = :testDeviceId AND envMapping.suiteId = testSuite.id " +
    "ORDER BY envMapping.position ASC")
  List<AbstractTestSuite> findAllByTestDeviceId(@Param("testDeviceId") Long testDeviceId);

  List<TestSuite> findAllByPreRequisite(Long prerequisite);

  @Query("SELECT testSuite FROM TestSuite testSuite " +
          " LEFT JOIN SuiteTestCaseMapping suiteTestCaseMapping ON testSuite.id = suiteTestCaseMapping.suiteId " +
          " WHERE suiteTestCaseMapping.testCaseId =:testCaseId")
  List<TestSuite> findAllByTestCaseId(Long testCaseId);

  @EntityGraph(attributePaths = {"testSuiteMappings"})
  @Query("SELECT testSuite FROM TestSuite AS testSuite " +
          "JOIN FETCH SuiteTestCaseMapping AS suiteCaseMapping " +
          "ON suiteCaseMapping.id.testCaseId = :testCaseId AND suiteCaseMapping.id.suiteId = testSuite.id " +
          "ORDER BY suiteCaseMapping.position ASC")
  List<TestSuite> findAllByTestCaseIds(@Param("testCaseId") Long testCaseId);

    Optional<TestSuite> findAllByWorkspaceVersionIdAndImportedId(Long workspaceId, Long id);
    Optional<TestSuite> findByNameAndWorkspaceVersionId(String name,Long workspaceId );

    List<TestSuite> findAllByWorkspaceVersionId(Long applicationVersionId);
}
