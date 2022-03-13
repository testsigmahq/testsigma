/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.controller;

import com.testsigma.dto.AdhocRunConfigurationDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.AdhocRunConfigurationsMapper;
import com.testsigma.model.AdhocRunConfiguration;
import com.testsigma.model.WorkspaceType;
import com.testsigma.service.AdhocRunConfigurationService;
import com.testsigma.web.request.AdhocRunConfigurationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/adhoc_run_configurations", produces = MediaType.APPLICATION_JSON_VALUE)
@Log4j2
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class AdhocRunConfigurationsController {
  private final AdhocRunConfigurationService adhocRunConfigurationService;
  private final AdhocRunConfigurationsMapper dryRunConfigurationMapper;

  @PostMapping
  public AdhocRunConfigurationDTO create(@RequestBody AdhocRunConfigurationRequest adhocRunConfigurationRequest) {
    AdhocRunConfiguration configSaved = adhocRunConfigurationService.create(adhocRunConfigurationRequest);
    return dryRunConfigurationMapper.map(configSaved);
  }


  @PutMapping(path = "/{id}")
  public AdhocRunConfigurationDTO update(
    @PathVariable(value = "id") Long id,
    @RequestBody AdhocRunConfigurationRequest adhocRunConfigurationRequest) throws ResourceNotFoundException {
    AdhocRunConfiguration adhocRunConfiguration = adhocRunConfigurationService.find(id);
    dryRunConfigurationMapper.map(adhocRunConfigurationRequest, adhocRunConfiguration);
    AdhocRunConfiguration configUpdated = adhocRunConfigurationService.update(adhocRunConfiguration);
    return dryRunConfigurationMapper.map(configUpdated);
  }

  @GetMapping(path = "/{appType}")
  public List<AdhocRunConfigurationDTO> index(@PathVariable(value = "appType") WorkspaceType appType) {
//    WorkspaceType workspaceType = WorkspaceType.getWorkspaceType(appType);
    List<AdhocRunConfiguration> dryRunConfigList = adhocRunConfigurationService.getDryRunConfigListByAppType(appType);
    return dryRunConfigurationMapper.map(dryRunConfigList);
  }

  @DeleteMapping(path = "/{id}")
  @ResponseStatus(value = HttpStatus.ACCEPTED)
  public void delete(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
    AdhocRunConfiguration adhocRunConfiguration = adhocRunConfigurationService.find(id);
    adhocRunConfigurationService.delete(adhocRunConfiguration);
  }
}
