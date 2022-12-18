/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.repository;

import com.testsigma.model.TestCasePriority;
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
public interface TestCasePriorityRepository extends PagingAndSortingRepository<TestCasePriority, Long>, JpaSpecificationExecutor<TestCasePriority>, JpaRepository<TestCasePriority, Long> {

    void deleteAllByWorkspaceId(@Param("projectId") Long projectId);

    Optional<TestCasePriority> findAllByWorkspaceIdAndName(Long id, String name);

    Optional<TestCasePriority> findAllByWorkspaceIdAndImportedId(Long projectId, Long id);

    List<TestCasePriority> findByWorkspaceId(Long workspaceId);
}
