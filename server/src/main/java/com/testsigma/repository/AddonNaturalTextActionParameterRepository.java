/******************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 *****************************************************************************/
package com.testsigma.repository;

import com.testsigma.model.AddonNaturalTextActionParameter;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface AddonNaturalTextActionParameterRepository extends BaseRepository<AddonNaturalTextActionParameter, Long> {
  @Modifying
  @Query("DELETE FROM AddonNaturalTextActionParameter WHERE pluginActionId=:addonId")
  void deleteAllByPluginActionId(@Param("addonId") Long addonId);

  List<AddonNaturalTextActionParameter> findAllByPluginActionId(Long addonId);
}
