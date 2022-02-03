/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.controller;

import com.testsigma.dto.AddonNaturalTextActionDTO;
import com.testsigma.dto.KibbutzPluginTestDataFunctionDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.AddonMapper;
import com.testsigma.model.AddonNaturalTextAction;
import com.testsigma.model.KibbutzPluginTestDataFunction;
import com.testsigma.service.AddonNaturalTextActionService;
import com.testsigma.service.KibbutzPluginTestDataFunctionService;
import com.testsigma.service.KibbutzService;
import com.testsigma.specification.AddonNaturalTextActionSpecificationsBuilder;
import com.testsigma.specification.KibbutzPluginTestDataFunctionSpecificationBuilder;
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
public class KibbutzAPIsController {
  private final KibbutzService kibbutzService;
  private final AddonNaturalTextActionService service;
  private final AddonMapper mapper;
  private final KibbutzPluginTestDataFunctionService testDataFunctionService;

  @RequestMapping(path = "/login", method = RequestMethod.GET)
  public void sso(@RequestParam(value = "redirect_uri", required = false) String redirectURI, HttpServletResponse response) throws IOException {
    log.debug("GET /kibbutz/login");
    URL url = kibbutzService.ssoURL(redirectURI);
    response.sendRedirect(String.valueOf(url));
  }

  @GetMapping(path = "/actions")
  public Page<AddonNaturalTextActionDTO> actions(AddonNaturalTextActionSpecificationsBuilder builder, @PageableDefault(size = Integer.MAX_VALUE) Pageable pageable) {
    log.debug("GET /kibbutz/actions");
    Specification<AddonNaturalTextAction> spec = builder.build();
    Page<AddonNaturalTextAction> actions = service.findAll(spec, pageable);
    List<AddonNaturalTextActionDTO> dtos = mapper.mapToDTO(actions.getContent());
    return new PageImpl<>(dtos, pageable, actions.getTotalElements());
  }


  @GetMapping(path = "/nlps/{id}")
  public AddonNaturalTextActionDTO nlp(@PathVariable("id") Long id) throws ResourceNotFoundException {
    log.debug("GET /kibbutz/nlps/"+id);
    AddonNaturalTextAction nlp = service.findById(id);
    return mapper.map(nlp);
  }

  @GetMapping(path = "/test_data_functions")
  public Page<KibbutzPluginTestDataFunctionDTO> testDataFunctions(KibbutzPluginTestDataFunctionSpecificationBuilder builder, @PageableDefault(size = Integer.MAX_VALUE) Pageable pageable) {
    log.debug("GET /kibbutz/test_data_functions");
    Specification<KibbutzPluginTestDataFunction> spec = builder.build();
    Page<KibbutzPluginTestDataFunction> nlps = testDataFunctionService.findAll(spec, pageable);
    List<KibbutzPluginTestDataFunctionDTO> dtos = mapper.mapTDFToDTO(nlps.getContent());
    return new PageImpl<>(dtos, pageable, nlps.getTotalElements());
  }

  @GetMapping(path = "/test_data_functions/{id}")
  public KibbutzPluginTestDataFunctionDTO testDataFunction(@PathVariable("id") Long id) throws ResourceNotFoundException {
    log.debug("GET /kibbutz/test_data_functions");
    KibbutzPluginTestDataFunction tdf = testDataFunctionService.findById(id);
    tdf.setExternalUniqueId(tdf.getPlugin().getExternalUniqueId());
    return mapper.map(tdf);
  }
}
