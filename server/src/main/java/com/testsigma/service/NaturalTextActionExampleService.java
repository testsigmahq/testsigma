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
import com.testsigma.model.NaturalTextActionExample;
import com.testsigma.repository.NaturalTextActionExamplesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
public class NaturalTextActionExampleService {

  private final NaturalTextActionExamplesRepository repository;

  public NaturalTextActionExample findByNaturalTextActionId(Long naturalTextActionId) throws ResourceNotFoundException {
    return this.repository.findByNaturalTextActionId(naturalTextActionId).orElseThrow(() -> new ResourceNotFoundException("Example missing"));
  }

}

