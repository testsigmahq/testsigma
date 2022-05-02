/******************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 *****************************************************************************/
package com.testsigma.repository;

import com.testsigma.model.Agent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface AgentRepository extends JpaSpecificationExecutor<Agent>, PagingAndSortingRepository<Agent, Long>, JpaRepository<Agent, Long> {

  Agent findByUniqueId(String uniqueId);

  Page<Agent> findAll(Specification specification, Pageable pageable);

    Optional<Agent> findAllByImportedId(Long id);
}
