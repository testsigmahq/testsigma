/******************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 *****************************************************************************/
package com.testsigma.repository;

import com.testsigma.model.TestDevice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;


@Repository
@Transactional
public interface TestDeviceRepository extends BaseRepository<TestDevice, Long> {
  List<TestDevice> findTestDeviceByAgentId(Long agentId);

  List<TestDevice> findByExecutionIdAndDisable(Long executionId, Boolean disable);

  Page<TestDevice> findAll(Specification<TestDevice> spec, Pageable pageable);

  List<TestDevice> findByExecutionId(Long executionId);

  @Modifying
  @Query("DELETE FROM TestDevice exeEnv WHERE exeEnv.id IN (:ids)")
  void deleteAllByIds(@Param("ids") Set<Long> ids);

  List<TestDevice> findAllByDeviceIdIn(List<Long> removedAgentDeviceIds);
}
