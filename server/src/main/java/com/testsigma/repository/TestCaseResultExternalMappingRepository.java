/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.repository;

import com.testsigma.model.TestCaseResultExternalMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface TestCaseResultExternalMappingRepository extends JpaRepository<TestCaseResultExternalMapping, Long> {
  List<TestCaseResultExternalMapping> findByTestCaseResultId(Long testCaseResultId);
}
