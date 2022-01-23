/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.controller.api.v1;

import com.testsigma.dto.api.APIElementDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.ElementMapper;
import com.testsigma.model.Element;
import com.testsigma.service.ElementService;
import com.testsigma.specification.ElementSpecificationsBuilder;
import com.testsigma.web.request.ElementRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController("apiElementsController")
@RequestMapping(path = "/api/v1/elements")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ElementsController {
  private final ElementService elementService;
  private final ElementMapper elementMapper;

  @RequestMapping(method = RequestMethod.POST)
  public APIElementDTO create(@RequestBody @Valid ElementRequest elementRequest) {
    Element element = elementMapper.map(elementRequest);
    elementService.create(element);
    return elementMapper.mapToApi(element);
  }

  @RequestMapping(method = RequestMethod.GET)
  public Page<APIElementDTO> index(ElementSpecificationsBuilder builder, Pageable pageable) {
    Specification<Element> spec = builder.build();
    Page<Element> elements = elementService.findAll(spec, pageable);
    List<APIElementDTO> elementDTOS = elementMapper.mapToApiList(elements.getContent());
    return new PageImpl<>(elementDTOS, pageable, elements.getTotalElements());
  }

  @RequestMapping(path = "/{id}", method = RequestMethod.GET)
  public APIElementDTO show(@PathVariable("id") Long id) throws ResourceNotFoundException {
    Element element = elementService.find(id);
    return elementMapper.mapToApi(element);
  }

  @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
  public APIElementDTO update(@PathVariable("id") Long id, @RequestBody ElementRequest elementRequest)
    throws ResourceNotFoundException {
    Element element = elementService.find(id);
    String oldName = element.getName();
    elementMapper.merge(elementRequest, element);
    elementService.update(element, oldName);
    return elementMapper.mapToApi(element);
  }

  @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.OK)
  public void delete(@PathVariable("id") Long id) throws ResourceNotFoundException {
    elementService.delete(elementService.find(id));
  }
}
