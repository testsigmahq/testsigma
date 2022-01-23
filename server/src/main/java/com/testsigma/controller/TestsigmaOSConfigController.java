/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.controller;

import com.testsigma.dto.OpensourceDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.mapper.TestsigmaOSConfigMapper;
import com.testsigma.model.TestsigmaOSConfig;
import com.testsigma.service.TestsigmaOSConfigService;
import com.testsigma.web.request.OnboardingRequest;
import com.testsigma.web.request.OpensourceRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/testsigma_os_config")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class TestsigmaOSConfigController {

  private final TestsigmaOSConfigMapper mapper;
  private final TestsigmaOSConfigService service;

  @RequestMapping(method = RequestMethod.GET)
  OpensourceDTO show() {
    TestsigmaOSConfig testsigmaOSConfig = service.find();
    if (testsigmaOSConfig == null)
      testsigmaOSConfig = new TestsigmaOSConfig();
    OpensourceDTO dto = mapper.map(testsigmaOSConfig);
    ;
    dto.setUrl(service.getTestsigmaOsProxyUrl());
    return dto;
  }

  @RequestMapping(method = RequestMethod.POST)
  public OpensourceDTO create(@RequestBody OpensourceRequest request) throws ResourceNotFoundException {
    TestsigmaOSConfig testsigmaOSConfig = service.find();
    if (testsigmaOSConfig == null)
      testsigmaOSConfig = new TestsigmaOSConfig();
    mapper.merge(request, testsigmaOSConfig);
    service.save(testsigmaOSConfig);
    return mapper.map(testsigmaOSConfig);
  }


  @RequestMapping(value = "/otp", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void getOTP(@RequestBody OnboardingRequest onboardingRequest) throws TestsigmaException {
    service.getOTP(onboardingRequest);
  }

  @RequestMapping(value = "/activate/{otp}", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void activate(@PathVariable("otp") String otp)
    throws TestsigmaException {
    service.activate(otp);
  }
}
