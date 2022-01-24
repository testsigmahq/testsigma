/*
 * *****************************************************************************
 *  Copyright (C) 2019 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.repository;

import com.testsigma.model.TestCaseFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface TestCaseFilterRepository extends JpaRepository<TestCaseFilter, Long> {
  @Query("SELECT testCaseFilter FROM TestCaseFilter testCaseFilter " +
    "WHERE (testCaseFilter.versionId =:versionId OR testCaseFilter.versionId IS NULL) ")
  Page<TestCaseFilter> findAll(@Param("versionId") Long versionId, Pageable pageable);
}
