/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.service;

import com.testsigma.mapper.SuggestionResultMappingMapper;
import com.testsigma.model.SuggestionResultMapping;
import com.testsigma.model.TestStepResult;
import com.testsigma.repository.SuggestionResultRepository;
import com.testsigma.web.request.SuggestionEngineResultRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SuggestionResultMappingService {
  private final SuggestionResultRepository repository;
  private final SuggestionResultMappingMapper mapper;

  public SuggestionResultMapping create(SuggestionEngineResultRequest request, TestStepResult testStepResult) {
    SuggestionResultMapping suggestionResultMapping = mapper.map(request);
    suggestionResultMapping.setStepResultId(testStepResult.getId());
    return this.repository.save(suggestionResultMapping);
  }

  public Page<SuggestionResultMapping> findAllByStepResultId(Long stepResultId, Pageable pageable) {
    return this.repository.findAllByStepResultId(stepResultId, pageable);
  }

}
