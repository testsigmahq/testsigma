/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.service;

import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.model.DefaultDataGenerator;
import com.testsigma.model.DefaultDataGeneratorFile;
import com.testsigma.repository.DefaultDataGeneratorFilesRepository;
import com.testsigma.repository.DefaultDataGeneratorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DefaultDataGeneratorService {

  private final DefaultDataGeneratorRepository defaultDataGeneratorRepository;
  private final DefaultDataGeneratorFilesRepository defaultDataGeneratorFilesRepository;

  public DefaultDataGeneratorFile findFileById(Long id) throws ResourceNotFoundException {
    return this.defaultDataGeneratorFilesRepository.findById(id).orElseThrow(()
      -> new ResourceNotFoundException("File missing with id" + id));
  }

  public Page<DefaultDataGenerator> findAll(Specification<DefaultDataGenerator> specification, Pageable pageable) {
    return this.defaultDataGeneratorRepository.findAll(specification, pageable);
  }

  public DefaultDataGenerator find(Long id) {
    return defaultDataGeneratorRepository.findById(id).get();
  }
}
