/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.controller;

import com.testsigma.dto.RequirementDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.RequirementMapper;
import com.testsigma.model.Requirement;
import com.testsigma.service.RequirementService;
import com.testsigma.specification.RequirementSpecificationsBuilder;
import com.testsigma.web.request.RequirementRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

@RestController
@RequestMapping("/requirements")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class RequirementsController {

  private final RequirementService service;
  private final RequirementMapper mapper;

  @RequestMapping(method = RequestMethod.GET)
  public Page<RequirementDTO> index(RequirementSpecificationsBuilder builder, Pageable pageable) {
    Specification<Requirement> spec = builder.build();
    Page<Requirement> page = service.findAll(spec, pageable);
    List<RequirementDTO> dtos = mapper.map(page.getContent());
    return new PageImpl<>(dtos, pageable, page.getTotalElements());
  }

  @GetMapping("/{id}")
  public RequirementDTO show(@PathVariable("id") Long id) throws ResourceNotFoundException {
    Requirement item = this.service.find(id);
    return mapper.map(item);
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public RequirementDTO update(@PathVariable("id") Long id, @RequestBody RequirementRequest request) throws ResourceNotFoundException {
    Requirement item = this.service.find(id);
    this.mapper.merge(request, item);
    item.setUpdatedDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    item = this.service.update(item);
    return mapper.map(item);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public RequirementDTO create(@RequestBody RequirementRequest request) throws ResourceNotFoundException {
    Requirement requirement = this.mapper.map(request);
    requirement.setCreatedDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    requirement = this.service.create(requirement);
    return mapper.map(requirement);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void destroy(@PathVariable("id") Long id) throws ResourceNotFoundException {
    this.service.destroy(id);
  }

  @DeleteMapping(value = "/bulk")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void bulkDelete(@RequestParam(value = "ids[]") Long[] ids) throws ResourceNotFoundException {
    for (Long id : ids) {
      this.service.destroy(id);
    }
  }

}
