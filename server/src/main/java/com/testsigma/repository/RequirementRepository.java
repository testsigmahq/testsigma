/*
 * *****************************************************************************
 *  Copyright (C) 2019 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.repository;

import com.testsigma.model.Requirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface RequirementRepository extends PagingAndSortingRepository<Requirement, Long>, JpaSpecificationExecutor<Requirement>, JpaRepository<Requirement, Long> {

  List<Requirement> findAllByWorkspaceVersionId(Long workspaceVersionId);

  @Modifying
  void deleteAllByWorkspaceVersionId(@Param("workspaceVersionId") Long workspaceVersionId);
}
