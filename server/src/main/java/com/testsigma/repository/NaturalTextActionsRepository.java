/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.repository;

import com.testsigma.model.WorkspaceType;
import com.testsigma.model.NaturalTextActions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface NaturalTextActionsRepository extends JpaRepository<NaturalTextActions, Long> {

  Page<NaturalTextActions> findAll(Specification<NaturalTextActions> spec, Pageable pageable);

  List<NaturalTextActions> findAllByDisplayName(String displayName);
}
