/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.repository;

import com.testsigma.model.AbstractTestSuite;
import com.testsigma.model.SuiteTestCaseMapping;
import com.testsigma.model.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface SuiteTestCaseMappingRepository extends JpaRepository<SuiteTestCaseMapping, Long> {

  Optional<SuiteTestCaseMapping> findFirstByTestSuiteAndTestCase(AbstractTestSuite testSuite, TestCase testCase);

  Optional<SuiteTestCaseMapping> findById(Long id);

  List<SuiteTestCaseMapping> findAllBySuiteId(Long suiteId);

  @Query("SELECT suiteTestCaseMapping FROM SuiteTestCaseMapping suiteTestCaseMapping WHERE suiteTestCaseMapping.testCaseId IN(:testCaseIds) and suiteTestCaseMapping.suiteId = :suiteId")
  List<SuiteTestCaseMapping> findBySuiteIdAndTestCaseIds(@Param("suiteId") Long suiteId, @Param("testCaseIds") List<Long> testCaseIds);

}
