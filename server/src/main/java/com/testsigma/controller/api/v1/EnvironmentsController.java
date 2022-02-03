package com.testsigma.controller.api.v1;

/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */


import com.testsigma.dto.api.APIEnvironmentDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.EnvironmentMapper;
import com.testsigma.model.Environment;
import com.testsigma.service.EnvironmentService;
import com.testsigma.specification.EnvironmentSpecificationsBuilder;
import com.testsigma.web.request.EnvironmentRequest;
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

@RestController(value = "apiEnvironmentsController")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(path = "/api/v1/environments")
public class EnvironmentsController {

  private final EnvironmentMapper environmentMapper;
  private final EnvironmentService environmentService;

  @RequestMapping(path = "/{id}", method = RequestMethod.GET)
  public APIEnvironmentDTO show(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
    log.info("Get Request /api/v1/environments/" + id);
    Environment environment = environmentService.find(id);
    return environmentMapper.mapApi(environment);
  }

  @RequestMapping(method = RequestMethod.GET)
  public Page<APIEnvironmentDTO> index(EnvironmentSpecificationsBuilder builder, Pageable pageable) {
    log.info("Get Request /api/v1/environments");
    Specification<Environment> spec = builder.build();
    Page<Environment> environments = environmentService.findAll(spec, pageable);
    List<APIEnvironmentDTO> environmentDTOS =
      environmentMapper.mapApi(environments.getContent());
    return new PageImpl<>(environmentDTOS, pageable, environments.getTotalElements());
  }

  @PutMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public APIEnvironmentDTO update(@PathVariable(value = "id") Long id, @RequestBody EnvironmentRequest request) throws ResourceNotFoundException {
    log.info("Update Request /api/v1/environments/" + id);
    Environment environment = environmentService.find(id);
    Environment oldEnvironment = new Environment();
    oldEnvironment.setData(environment.getData());
    environmentMapper.merge(environment, request);
    environment.setUpdatedDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    environment = this.environmentService.update(environment);
    return environmentMapper.mapApi(environment);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public APIEnvironmentDTO create(@RequestBody EnvironmentRequest request) throws ResourceNotFoundException {
    log.info("Create Request /api/v1/environments/" + request);
    Environment environment = environmentMapper.map(request);
    environment.setCreatedDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    environment = this.environmentService.create(environment);
    return environmentMapper.mapApi(environment);
  }
}

