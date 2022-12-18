/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.repository;

import com.testsigma.model.TestCaseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface TestCaseTypeRepository extends PagingAndSortingRepository<TestCaseType, Long>, JpaSpecificationExecutor<TestCaseType>, JpaRepository<TestCaseType, Long> {
    void deleteAllByWorkspaceId(@Param("workspaceId") Long projectId);

    Optional<TestCaseType> findAllByWorkspaceIdAndImportedId(Long projectId, Long id);

    Optional<TestCaseType> findAllByWorkspaceIdAndName(Long projectId, String name);

    List<TestCaseType> findByWorkspaceId(Long workspaceId);

}
