/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.repository;

import com.testsigma.model.TestCaseDataDrivenResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface TestCaseDataDrivenResultRepository extends JpaRepository<TestCaseDataDrivenResult, Long> {

  void deleteByIterationResultId(Long iterationResultId);

  Page<TestCaseDataDrivenResult> findAll(Specification<TestCaseDataDrivenResult> spec, Pageable pageable);
}
