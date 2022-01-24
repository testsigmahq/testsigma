/*
 * *****************************************************************************
 *  Copyright (C) 2019 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.repository;

import com.testsigma.model.ElementFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface ElementFilterRepository extends JpaRepository<ElementFilter, Long> {

  @Query("SELECT elementFilter FROM ElementFilter elementFilter " +
    "WHERE (elementFilter.versionId =:versionId OR elementFilter.versionId IS NULL) ")
  Page<ElementFilter> findAll(@Param("versionId") Long versionId, Pageable pageable);
}
