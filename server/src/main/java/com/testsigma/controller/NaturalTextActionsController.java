/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.controller;

import com.testsigma.dto.NaturalTextActionsDTO;
import com.testsigma.dto.NaturaltextActionExampleDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.NaturalTextActionMapper;
import com.testsigma.model.NaturalTextActions;
import com.testsigma.model.NaturalTextActionExample;
import com.testsigma.service.NaturalTextActionExampleService;
import com.testsigma.service.NaturalTextActionsService;
import com.testsigma.specification.NaturalTextActionSpecificationsBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Log4j2
@RequestMapping(path = "/natural_text_actions", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class NaturalTextActionsController {
  private final NaturalTextActionsService naturalTextActionsService;
  private final NaturalTextActionMapper naturalTextActionMapper;
  private final NaturalTextActionExampleService naturaltextActionExampleService;

  @GetMapping
  public Page<NaturalTextActionsDTO> index(NaturalTextActionSpecificationsBuilder builder, @PageableDefault(size = Integer.MAX_VALUE) Pageable pageable) {
    log.info("Request /natural_text_actions/");
    Specification<NaturalTextActions> spec = builder.build();
    Page<NaturalTextActions> nlActions = naturalTextActionsService.findAll(spec, pageable);
    List<NaturalTextActionsDTO> dtos = naturalTextActionMapper.mapDTO(nlActions.getContent());
    return new PageImpl<>(dtos, pageable, nlActions.getTotalElements());
  }

  @GetMapping(path = "/{id}/example")
  public NaturaltextActionExampleDTO example(@PathVariable("id") Long naturalTextActionId) throws ResourceNotFoundException {
    NaturalTextActionExample example = naturaltextActionExampleService.findByNaturalTextActionId(naturalTextActionId);
    return naturalTextActionMapper.map(example);
  }
}
