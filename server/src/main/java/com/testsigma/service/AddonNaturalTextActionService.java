/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.service;

import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.AddonMapper;
import com.testsigma.model.AddonNaturalTextAction;
import com.testsigma.model.AddonNaturalTextActionParameter;
import com.testsigma.model.NaturalTextActions;
import com.testsigma.model.WorkspaceType;
import com.testsigma.repository.AddonNaturalTextActionParameterRepository;
import com.testsigma.repository.AddonNaturalTextActionRepository;
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
public class AddonNaturalTextActionService {
  private final AddonNaturalTextActionRepository repository;
  private final AddonNaturalTextActionParameterRepository parameterRepository;
  private final AddonMapper mapper;

  public void create(AddonNaturalTextAction action) {
    AddonNaturalTextAction actiondb = fetch(action);
    List<AddonNaturalTextActionParameter> parameters = action.getParameters();
    actiondb = repository.save(actiondb);
    parameterRepository.deleteAllByPluginActionId(actiondb.getId());
    for (AddonNaturalTextActionParameter parameter : parameters) {
      parameter.setPluginActionId(actiondb.getId());
      parameterRepository.save(parameter);
    }
  }

  public AddonNaturalTextAction fetch(AddonNaturalTextAction action) {
    Optional<AddonNaturalTextAction> actionOptional = repository.findByAddonIdAndFullyQualifiedName(action.getAddonId(), action.getFullyQualifiedName());
    AddonNaturalTextAction actionDB;
    if (actionOptional.isPresent()) {
      actionDB = actionOptional.get();
      mapper.merge(action, actionDB);
    } else
      actionDB = action;
    return actionDB;
  }

  public List<AddonNaturalTextAction> findAllByAddonId(Long addonId) {
    return repository.findAllByAddonId(addonId);
  }

  public AddonNaturalTextAction findById(Long id) throws ResourceNotFoundException {
    return this.repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("AddonNaturalTextAction missing::" + id));
  }

  public Page<AddonNaturalTextAction> findAllByWorkspaceType(WorkspaceType workspaceType, Pageable pageable) {
    return this.repository.findAllByWorkspaceType(workspaceType, pageable);
  }

  public Page<AddonNaturalTextAction> findAll(Specification<AddonNaturalTextAction> spec, Pageable pageable) {
    return this.repository.findAll(spec, pageable);
  }
}

