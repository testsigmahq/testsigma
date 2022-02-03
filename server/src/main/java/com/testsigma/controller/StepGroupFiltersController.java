/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.controller;

import com.testsigma.dto.StepGroupFilterDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.StepGroupFilterMapper;
import com.testsigma.model.StepGroupFilter;
import com.testsigma.service.StepGroupFilterService;
import com.testsigma.web.request.StepGroupFilterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/step_group_filters")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class StepGroupFiltersController {

  private final StepGroupFilterService service;
  private final StepGroupFilterMapper mapper;

  @GetMapping(path = "/{id}")
  public StepGroupFilterDTO show(@PathVariable("id") Long id) throws ResourceNotFoundException {
    log.info("Get Request /step_group_filters/" + id);
    StepGroupFilter filter = service.find(id);
    return mapper.map(filter);
  }

  @PutMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public StepGroupFilterDTO update(@PathVariable("id") Long id, @RequestBody StepGroupFilterRequest request) throws ResourceNotFoundException {
    log.info("Put Request /step_group_filters/" + id + " data:" + request);
    StepGroupFilter filter = service.find(id);
    mapper.merge(filter, request);
    filter = service.update(filter);
    return mapper.map(filter);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public StepGroupFilterDTO create(@RequestBody StepGroupFilterRequest request) throws ResourceNotFoundException {
    log.info("Post Request /step_group_filters/ data:" + request);
    StepGroupFilter filter = mapper.map(request);
    filter = service.create(filter);
    return mapper.map(filter);
  }

  @DeleteMapping(value = "/{id}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void destroy(@PathVariable("id") Long id) throws ResourceNotFoundException {
    log.info("Delete Request /step_group_filters/" + id);
    service.destroy(id);
  }

  @GetMapping
  public Page<StepGroupFilterDTO> index(@RequestParam(value = "versionId") Long versionId, @PageableDefault(size = 50) Pageable pageable) {
    log.info("Get Request /step_group_filters/ versionId:" + versionId);
    Page<StepGroupFilter> elements = service.findAllVisible(versionId, pageable);
    List<StepGroupFilterDTO> elementDTOS = mapper.map(elements.getContent());
    return new PageImpl<>(elementDTOS, pageable, elements.getTotalElements());
  }
}
