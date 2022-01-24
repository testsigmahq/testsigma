/******************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 *****************************************************************************/
package com.testsigma.repository;

import com.testsigma.model.AddonNaturalTextAction;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface AddonNaturalTextActionRepository extends BaseRepository<AddonNaturalTextAction, Long> {
  Optional<AddonNaturalTextAction> findByAddonIdAndFullyQualifiedName(Long addonId, String fullyQualifiedName);

  List<AddonNaturalTextAction> findAllByAddonId(Long addonId);
}
