/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.controller;

import com.testsigma.dto.AddonNaturalTextActionDTO;
import com.testsigma.dto.AddonPluginTestDataFunctionDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.AddonMapper;
import com.testsigma.model.AddonNaturalTextAction;
import com.testsigma.model.AddonPluginTestDataFunction;
import com.testsigma.service.AddonNaturalTextActionService;
import com.testsigma.service.AddonPluginTestDataFunctionService;
import com.testsigma.service.ProxyAddonService;
import com.testsigma.specification.AddonNaturalTextActionSpecificationsBuilder;
import com.testsigma.specification.AddonPluginTestDataFunctionSpecificationBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.List;

@Log4j2
@RestController
@RequestMapping(value = {"/kibbutz"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AddonAPIsController {
  private final ProxyAddonService addonService;
  private final AddonNaturalTextActionService service;
  private final AddonMapper mapper;
  private final AddonPluginTestDataFunctionService testDataFunctionService;

  @RequestMapping(path = "/login", method = RequestMethod.GET)
  public void sso(@RequestParam(value = "redirect_uri", required = false) String redirectURI, HttpServletResponse response) throws IOException {
    log.debug("GET /addon/login");
    URL url = addonService.ssoURL(redirectURI);
    response.sendRedirect(String.valueOf(url));
  }

  @GetMapping(path = "/actions")
  public Page<AddonNaturalTextActionDTO> actions(AddonNaturalTextActionSpecificationsBuilder builder, @PageableDefault(size = Integer.MAX_VALUE) Pageable pageable) {
    log.debug("GET /addon/actions");
    Specification<AddonNaturalTextAction> spec = builder.build();
    Page<AddonNaturalTextAction> actions = service.findAll(spec, pageable);
    List<AddonNaturalTextActionDTO> dtos = mapper.mapToDTO(actions.getContent());
    return new PageImpl<>(dtos, pageable, actions.getTotalElements());
  }


  @GetMapping(path = "/actions/{id}")
  public AddonNaturalTextActionDTO action(@PathVariable("id") Long id) throws ResourceNotFoundException {
    log.debug("GET /addon/actions/"+id);
    AddonNaturalTextAction action = service.findById(id);
    return mapper.map(action);
  }

  @GetMapping(path = "/test_data_functions")
  public Page<AddonPluginTestDataFunctionDTO> testDataFunctions(AddonPluginTestDataFunctionSpecificationBuilder builder, @PageableDefault(size = Integer.MAX_VALUE) Pageable pageable) {
    log.debug("GET /addon/test_data_functions");
    Specification<AddonPluginTestDataFunction> spec = builder.build();
    Page<AddonPluginTestDataFunction> actions = testDataFunctionService.findAll(spec, pageable);
    List<AddonPluginTestDataFunctionDTO> dtos = mapper.mapTDFToDTO(actions.getContent());
    return new PageImpl<>(dtos, pageable, actions.getTotalElements());
  }

  @GetMapping(path = "/test_data_functions/{id}")
  public AddonPluginTestDataFunctionDTO testDataFunction(@PathVariable("id") Long id) throws ResourceNotFoundException {
    log.debug("GET /addon/test_data_functions");
    AddonPluginTestDataFunction tdf = testDataFunctionService.findById(id);
    tdf.setExternalUniqueId(tdf.getPlugin().getExternalUniqueId());
    return mapper.map(tdf);
  }
}
