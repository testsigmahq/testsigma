/******************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 *****************************************************************************/
package com.testsigma.repository;

import com.testsigma.model.TestsigmaOSConfig;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface TestsigmaOSConfigRepository extends BaseRepository<TestsigmaOSConfig, Long> {
  TestsigmaOSConfig findFirstByIdIsNotNull();
}
