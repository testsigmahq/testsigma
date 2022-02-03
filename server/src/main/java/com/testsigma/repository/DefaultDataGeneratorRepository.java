/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.repository;

import com.testsigma.model.DefaultDataGenerator;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface DefaultDataGeneratorRepository extends BaseRepository<DefaultDataGenerator, Long> {
}
