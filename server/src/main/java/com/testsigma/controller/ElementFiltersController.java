/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.controller;

import com.testsigma.dto.ElementFilterDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.ElementFilterMapper;
import com.testsigma.model.ElementFilter;
import com.testsigma.service.ElementFilterService;
import com.testsigma.web.request.ElementFilterRequest;
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
@RequestMapping(path = "/element_filters")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class ElementFiltersController {

  private final ElementFilterService service;
  private final ElementFilterMapper mapper;

  @GetMapping(path = "/{id}")
  public ElementFilterDTO show(@PathVariable("id") Long id) throws ResourceNotFoundException {
    log.info("Get Request /element_filters/" + id);
    ElementFilter filter = service.find(id);
    return mapper.map(filter);
  }

  @PutMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public ElementFilterDTO update(@PathVariable("id") Long id, @RequestBody ElementFilterRequest request) throws ResourceNotFoundException {
    log.info("Put Request /element_filters/" + id + " data:" + request);
    ElementFilter filter = service.find(id);
    mapper.merge(filter, request);
    filter = service.normalizeFilter(filter).update(filter);
    return mapper.map(filter);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ElementFilterDTO create(@RequestBody ElementFilterRequest request) throws ResourceNotFoundException {
    log.info("Post Request /element_filters/ data:" + request);
    ElementFilter filter = mapper.map(request);
    filter = service.normalizeFilter(filter).create(filter);
    return mapper.map(filter);
  }

  @DeleteMapping(value = "/{id}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void destroy(@PathVariable("id") Long id) throws ResourceNotFoundException {
    log.info("Delete Request /element_filters/" + id);
    service.destroy(id);
  }

  @GetMapping
  public Page<ElementFilterDTO> index(@RequestParam(value = "versionId") Long versionId, @PageableDefault(size = 50) Pageable pageable) {
    Page<ElementFilter> elements = service.findAllVisible(versionId, pageable);
    List<ElementFilterDTO> elementDTOS = mapper.map(elements.getContent());
    return new PageImpl<>(elementDTOS, pageable, elements.getTotalElements());
  }
}
