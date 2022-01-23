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
import com.testsigma.model.StepGroupFilter;
import com.testsigma.repository.StepGroupFilterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StepGroupFilterService {

  private final StepGroupFilterRepository repository;

  public Page<StepGroupFilter> findAllVisible(Long versionId, Pageable pageable) {
    return repository.findAll(versionId, pageable);
  }

  public StepGroupFilter find(Long id) throws ResourceNotFoundException {
    return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("TestCaseFilter missing with" + id));
  }

  public StepGroupFilter create(StepGroupFilter testCaseFilter) {
    return repository.save(testCaseFilter);
  }

  public StepGroupFilter update(StepGroupFilter testCaseFilter) {
    return repository.save(testCaseFilter);
  }

  public void destroy(Long id) throws ResourceNotFoundException {
    StepGroupFilter testCaseFilter = find(id);
    repository.delete(testCaseFilter);
  }

}

