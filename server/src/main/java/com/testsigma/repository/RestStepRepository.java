/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.repository;

import com.testsigma.model.RestStep;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestStepRepository extends JpaRepository<RestStep, Long> {
  RestStep findByStepId(Long stepId);
}
