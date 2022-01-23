/*
 * *****************************************************************************
 *  Copyright (C) 2019 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.repository;

import com.testsigma.model.NaturalTextActionExample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface NaturalTextActionExamplesRepository extends JpaRepository<NaturalTextActionExample, Long> {
  Optional<NaturalTextActionExample> findByNaturalTextActionId(Long NaturalTextActionId);
}
