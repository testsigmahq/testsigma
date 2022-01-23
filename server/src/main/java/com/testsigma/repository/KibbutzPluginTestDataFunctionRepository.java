/******************************************************************************
 * Copyright (C) 2019 Testsigma Inc.
 * All rights reserved.
 *****************************************************************************/
package com.testsigma.repository;

import com.testsigma.model.KibbutzPluginTestDataFunction;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface KibbutzPluginTestDataFunctionRepository extends BaseRepository<KibbutzPluginTestDataFunction, Long> {
  Optional<KibbutzPluginTestDataFunction> findByAddonIdAndFullyQualifiedName(Long addonId, String fullyQualifiedName);

  List<KibbutzPluginTestDataFunction> findAllByAddonId(Long addonId);
}
