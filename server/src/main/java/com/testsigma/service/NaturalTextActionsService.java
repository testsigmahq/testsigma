/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.service;


import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.model.NaturalTextActions;
import com.testsigma.repository.NaturalTextActionsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "naturalTextActionsService")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NaturalTextActionsService {
  private final NaturalTextActionsRepository naturalTextActionsRepository;

  public Page<NaturalTextActions> findAll(Specification<NaturalTextActions> spec, Pageable pageable) {
    return naturalTextActionsRepository.findAll(spec, pageable);
  }

  public NaturalTextActions findById(Long naturalTextActionId) throws ResourceNotFoundException {
    return this.naturalTextActionsRepository.findById(naturalTextActionId).orElseThrow(() -> new ResourceNotFoundException("NaturalTextAction missing::" + naturalTextActionId));
  }

  public List<NaturalTextActions> findByDisplayName(String displayName) {
    return this.naturalTextActionsRepository.findAllByDisplayName(displayName);
  }

}
