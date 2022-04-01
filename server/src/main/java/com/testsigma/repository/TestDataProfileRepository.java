/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.repository;

import com.testsigma.model.TestData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface TestDataProfileRepository extends JpaRepository<TestData, Long>, JpaSpecificationExecutor<TestData> {

  @Query("SELECT td FROM TestData td " +
    "JOIN FETCH td.testCases tc " +
    "WHERE tc.id = :testCaseId")
  Optional<TestData> findTestDataByTestCaseId(@Param("testCaseId") Long testCaseId);

  Optional<TestData> findByTestDataNameAndVersionId(String testDataName, Long versionId);

  List<TestData> findAllByVersionId(Long workspaceVersionId);

  @Query(value = "SELECT COUNT(*) FROM test_cases tc JOIN test_steps ts ON tc.id =ts.test_case_id WHERE tc.workspace_version_id =:workspaceVersionId AND ts.for_loop_test_data_id =:testDataProfileId",nativeQuery = true)
  Long countAllTestDataProfilesUsedInForLoopSteps(Long workspaceVersionId,Long testDataProfileId);
}
