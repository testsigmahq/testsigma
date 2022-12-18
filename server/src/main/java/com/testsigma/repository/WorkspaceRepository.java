/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.repository;

import com.testsigma.model.Workspace;
import com.testsigma.model.WorkspaceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface WorkspaceRepository extends JpaRepository<Workspace, Long>, JpaSpecificationExecutor<Workspace>, PagingAndSortingRepository<Workspace, Long> {

  Workspace findFirstByIsDemoAndWorkspaceType(Boolean isDemo, WorkspaceType workspaceType);

  Workspace findByWorkspaceTypeAndIsDemo(WorkspaceType workspaceType, Boolean isDemo);
}
