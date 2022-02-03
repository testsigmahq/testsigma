/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.repository;

import com.testsigma.model.AgentDevice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface AgentDeviceRepository extends PagingAndSortingRepository<AgentDevice, Long> {

  List<AgentDevice> findAllByAgentId(Long agentId);

  Page<AgentDevice> findAllByAgentId(Long agentId, Pageable pageable);

  Optional<AgentDevice> findAgentDeviceByAgentIdAndUniqueId(Long agentId, String uniqueId);

  Page<AgentDevice> findAllByAgentIdAndIsOnline(Long agentId, Boolean isOnline, Pageable pageable);

  @Modifying
  @Query("UPDATE AgentDevice ad SET ad.isOnline = false WHERE ad.agentId = :agentId ")
  void updateAgentDevice(@Param("agentId") Long agentId);

  List<AgentDevice> findAllByUniqueId(String deviceUDID);
}
