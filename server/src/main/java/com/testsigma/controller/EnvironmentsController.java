/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.controller;

import com.testsigma.dto.EnvironmentDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.EnvironmentMapper;
import com.testsigma.model.Environment;
import com.testsigma.service.EnvironmentService;
import com.testsigma.specification.EnvironmentSpecificationsBuilder;
import com.testsigma.web.request.EnvironmentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
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
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(path = "/environments")
public class EnvironmentsController {

  private final EnvironmentMapper environmentMapper;
  private final EnvironmentService environmentService;

  @RequestMapping(path = "/{id}", method = RequestMethod.GET)
  public EnvironmentDTO show(@PathVariable(value = "id") Long id, @RequestParam(value = "encrypt", required = false) Boolean encrypt) throws ResourceNotFoundException {
    log.info("Get Request /environments/" + id);
    Environment environment = environmentService.find(id);
    EnvironmentDTO environmentDTO = environmentMapper.map(environment);
    if (encrypt != null && !encrypt)
      environmentDTO.setParameters(new JSONObject(environment.decryptedData()));
    return environmentDTO;
  }

  @RequestMapping(method = RequestMethod.GET)
  public Page<EnvironmentDTO> index(EnvironmentSpecificationsBuilder builder, Pageable pageable) {
    log.info("Get Request /environments");
    Specification<Environment> spec = builder.build();
    Page<Environment> environments = environmentService.findAll(spec, pageable);
    List<EnvironmentDTO> environmentDTOS =
      environmentMapper.map(environments.getContent());
    return new PageImpl<>(environmentDTOS, pageable, environments.getTotalElements());
  }

  @DeleteMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void destroy(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
    log.info("Delete Request /environments/" + id);
    environmentService.destroy(id);
  }

  @DeleteMapping(value = "/bulk")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void bulkDelete(@RequestParam(value = "ids[]") Long[] ids) throws Exception {
    environmentService.bulkDestroy(ids);
  }

  @PutMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public EnvironmentDTO update(@PathVariable(value = "id") Long id, @RequestBody EnvironmentRequest request) throws ResourceNotFoundException {
    log.info("Update Request /environments/" + id);
    Environment environment = environmentService.find(id);
    Environment oldEnvironment = new Environment();
    oldEnvironment.setPasswords(environment.getPasswords());
    oldEnvironment.setData(environment.getData());
    environmentMapper.merge(environment, request);
    environment.setUpdatedDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    environment.updatePasswordValue(oldEnvironment);
    environment = this.environmentService.update(environment);
    return environmentMapper.map(environment);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public EnvironmentDTO create(@RequestBody EnvironmentRequest request) throws ResourceNotFoundException {
    log.info("Create Request /environments/" + request);
    Environment environment = environmentMapper.map(request);
    environment.setCreatedDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    environment.encryptPasswords();
    environment = this.environmentService.create(environment);
    return environmentMapper.map(environment);
  }
}
