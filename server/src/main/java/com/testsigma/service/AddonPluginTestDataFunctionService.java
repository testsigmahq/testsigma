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
import com.testsigma.model.AddonPluginTestDataFunction;
import com.testsigma.model.AddonPluginTestDataFunctionParameter;
import com.testsigma.repository.AddonPluginTestDataFunctionParameterRepository;
import com.testsigma.repository.AddonPluginTestDataFunctionRepository;
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
public class AddonPluginTestDataFunctionService {
  private final AddonPluginTestDataFunctionRepository repository;
  private final AddonPluginTestDataFunctionParameterRepository parameterRepository;
  private final AddonMapper mapper;

  public void create(AddonPluginTestDataFunction tdf) {
    AddonPluginTestDataFunction tdfDB = fetch(tdf);
    List<AddonPluginTestDataFunctionParameter> parameters = tdf.getParameters();
    tdfDB = repository.save(tdfDB);
    parameterRepository.deleteAllByTestDataFunctionId(tdfDB.getId());
    for(AddonPluginTestDataFunctionParameter parameter : parameters) {
      parameter.setTestDataFunctionId(tdfDB.getId());
      parameterRepository.save(parameter);
    }
  }

  public AddonPluginTestDataFunction fetch(AddonPluginTestDataFunction tdf) {
    Optional<AddonPluginTestDataFunction> tdfOptional = repository.findByAddonIdAndFullyQualifiedName(tdf.getAddonId(), tdf.getFullyQualifiedName());
    AddonPluginTestDataFunction tdfDB;
    if(tdfOptional.isPresent()) {
      tdfDB = tdfOptional.get();
      mapper.merge(tdf, tdfDB);
    } else
      tdfDB = tdf;
    return tdfDB;
  }

  public List<AddonPluginTestDataFunction> findAllByAddonId(Long addonId) {
    return repository.findAllByAddonId(addonId);
  }

  public AddonPluginTestDataFunction findById(Long id) throws ResourceNotFoundException {
    return this.repository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Addon Plugin TestData Function missing::"+id));
  }

  public Page<AddonPluginTestDataFunction> findAll(Specification<AddonPluginTestDataFunction> spec, Pageable pageable) {
    return this.repository.findAll(spec, pageable);
  }
}
