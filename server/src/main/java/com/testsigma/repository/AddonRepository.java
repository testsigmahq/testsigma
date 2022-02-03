/******************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 *****************************************************************************/
package com.testsigma.repository;

import com.testsigma.model.Addon;
import com.testsigma.model.AddonStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface AddonRepository extends BaseRepository<Addon, Long> {
  Optional<Addon> findByExternalUniqueIdAndStatus(String externalUniqueId, AddonStatus status);
  Optional<Addon> findTopByExternalUniqueIdAndStatus(String externalUniqueId, AddonStatus status);

}
