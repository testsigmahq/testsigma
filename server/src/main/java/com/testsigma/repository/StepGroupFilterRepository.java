/*
 * *****************************************************************************
 *  Copyright (C) 2019 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.repository;

import com.testsigma.model.StepGroupFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface StepGroupFilterRepository extends JpaRepository<StepGroupFilter, Long> {
  @Query("SELECT stepGroupFilter FROM StepGroupFilter stepGroupFilter " +
    "WHERE (stepGroupFilter.versionId =:versionId OR stepGroupFilter.versionId IS NULL) ")
  Page<StepGroupFilter> findAll(@Param("versionId") Long versionId, Pageable pageable);
}
