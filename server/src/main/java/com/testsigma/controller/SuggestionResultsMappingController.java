/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.controller;

import com.testsigma.dto.SuggestionResultMappingDTO;
import com.testsigma.mapper.SuggestionResultMappingMapper;
import com.testsigma.model.SuggestionResultMapping;
import com.testsigma.service.SuggestionResultMappingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(path = "/suggestion_results", produces = MediaType.APPLICATION_JSON_VALUE, consumes =
  MediaType.APPLICATION_JSON_VALUE)
@Log4j2
public class SuggestionResultsMappingController {
  private final SuggestionResultMappingMapper mapper;
  private final SuggestionResultMappingService service;


  @GetMapping
  private Page<SuggestionResultMappingDTO> index(@RequestParam("stepResultId") Long stepResultId, Pageable pageable) {
    log.info("Get Request /suggestion_results/ stepResultId:" + stepResultId);
    Page<SuggestionResultMapping> suggestionResults = service.findAllByStepResultId(stepResultId, pageable);
    List<SuggestionResultMappingDTO> suggestionResultMappingDTOS = mapper.map(suggestionResults.getContent());
    return new PageImpl<>(suggestionResultMappingDTOS, pageable, suggestionResults.getTotalElements());
  }
}
