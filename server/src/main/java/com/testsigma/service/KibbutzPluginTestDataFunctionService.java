/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.service;

import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.AddonMapper;
import com.testsigma.model.KibbutzPluginTestDataFunction;
import com.testsigma.model.KibbutzPluginTestDataFunctionParameter;
import com.testsigma.repository.KibbutzPluginTestDataFunctionParameterRepository;
import com.testsigma.repository.KibbutzPluginTestDataFunctionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
public class KibbutzPluginTestDataFunctionService {
  private final KibbutzPluginTestDataFunctionRepository repository;
  private final KibbutzPluginTestDataFunctionParameterRepository parameterRepository;
  private final AddonMapper mapper;

  public void create(KibbutzPluginTestDataFunction tdf) {
    KibbutzPluginTestDataFunction tdfDB = fetch(tdf);
    List<KibbutzPluginTestDataFunctionParameter> parameters = tdf.getParameters();
    tdfDB = repository.save(tdfDB);
    parameterRepository.deleteAllByTestDataFunctionId(tdfDB.getId());
    for(KibbutzPluginTestDataFunctionParameter parameter : parameters) {
      parameter.setTestDataFunctionId(tdfDB.getId());
      parameterRepository.save(parameter);
    }
  }

  public KibbutzPluginTestDataFunction fetch(KibbutzPluginTestDataFunction tdf) {
    Optional<KibbutzPluginTestDataFunction> tdfOptional = repository.findByAddonIdAndFullyQualifiedName(tdf.getAddonId(), tdf.getFullyQualifiedName());
    KibbutzPluginTestDataFunction tdfDB;
    if(tdfOptional.isPresent()) {
      tdfDB = tdfOptional.get();
      mapper.merge(tdf, tdfDB);
    } else
      tdfDB = tdf;
    return tdfDB;
  }

  public List<KibbutzPluginTestDataFunction> findAllByAddonId(Long addonId) {
    return repository.findAllByAddonId(addonId);
  }

  public KibbutzPluginTestDataFunction findById(Long id) throws ResourceNotFoundException {
    return this.repository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Kibbutz Plugin TestData Function missing::"+id));
  }

  public Page<KibbutzPluginTestDataFunction> findAll(Specification<KibbutzPluginTestDataFunction> spec, Pageable pageable) {
    return this.repository.findAll(spec, pageable);
  }
}
