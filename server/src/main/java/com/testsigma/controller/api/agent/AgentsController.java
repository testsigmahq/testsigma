/*
 * *****************************************************************************
 *  Copyright (C) 2019 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.controller.api.agent;

import com.fasterxml.jackson.core.type.TypeReference;
import com.testsigma.config.URLConstants;
import com.testsigma.dto.AgentDTO;
import com.testsigma.dto.AgentExecutionDTO;
import com.testsigma.dto.AgentWebServerConfigDTO;
import com.testsigma.dto.EnvironmentEntityDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.mapper.AgentMapper;
import com.testsigma.model.*;
import com.testsigma.service.AgentService;
import com.testsigma.service.PlatformsService;
import com.testsigma.service.TestDeviceResultService;
import com.testsigma.service.TestsigmaOSConfigService;
import com.testsigma.util.HttpClient;
import com.testsigma.util.HttpResponse;
import com.testsigma.web.request.AgentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController(value = "agentAgentsController")
@RequestMapping(path = "/api/agents")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class AgentsController {
  private final AgentService agentService;
  private final AgentMapper mapper;
  private final TestDeviceResultService testDeviceResultService;
  private final PlatformsService platformService;
  private final HttpClient httpClient;
  private final TestsigmaOSConfigService testsigmaOSConfigService;

  @RequestMapping(path = "/{uuid}", method = RequestMethod.GET)
  public AgentDTO show(@PathVariable("uuid") String uniqueId)
    throws ResourceNotFoundException {
    Agent agent = agentService.findByUniqueId(uniqueId);
    return mapper.map(agent);
  }

  @RequestMapping(path = "/{uuid}", method = RequestMethod.PUT)
  public AgentDTO update(@RequestBody AgentRequest agentRequest, @PathVariable("uuid") String uniqueId)
    throws ResourceNotFoundException {
    log.info("Request /api/agents/" + uniqueId + " received with data: " + agentRequest.toString());
    Agent agent = agentService.update(agentRequest, uniqueId);
    return mapper.map(agent);
  }

  @RequestMapping(path = "/{uuid}/execution", method = RequestMethod.GET)
  public AgentExecutionDTO getExecution(HttpServletResponse response, @PathVariable("uuid") String uniqueId)
    throws Exception {
    log.info("Request /api/agents/" + uniqueId + "/test_plans received");

    EnvironmentEntityDTO environmentEntityDTO = null;

    Agent agent = agentService.findByUniqueId(uniqueId);
    TestDeviceResult testDeviceResult =
      testDeviceResultService.findQueuedHybridEnvironment(agent.getId());
    if (testDeviceResult != null) {
      List<EnvironmentEntityDTO> environmentEntityDTOs = testDeviceResultService.getHybridEnvironmentEntitiesInPreFlight(new ArrayList<>() {{
        add(testDeviceResult);
      }});
      if (environmentEntityDTOs.size() > 0) {
        environmentEntityDTO = environmentEntityDTOs.get(0);
        environmentEntityDTO = environmentEntityDTO.getTestSuites() != null && environmentEntityDTO.getTestSuites().size() == 0 ? null : environmentEntityDTO;
      }
    }
    AgentExecutionDTO executionDTO = new AgentExecutionDTO();
    executionDTO.setEnvironment(environmentEntityDTO);
    response.setHeader("X-Request-Id", ThreadContext.get("X-Request-Id"));

    return executionDTO;
  }


  @RequestMapping(path = "/certificate", method = RequestMethod.GET)
  public AgentWebServerConfigDTO getWebServerCertificate() throws TestsigmaException {
    HttpResponse<AgentWebServerConfigDTO> response = httpClient.get(testsigmaOSConfigService.getUrl() +
      URLConstants.TESTSIGMA_OS_PUBLIC_CERTIFICATE_URL, getHeaders(), new TypeReference<>() {
    });
    return response.getResponseEntity();
  }

  @RequestMapping(path = "/{uuid}/driver/executable_path", method = RequestMethod.GET)
  public String getExecutablePath(@PathVariable("uuid") String uniqueId,
                                  @RequestParam("browserName") String browserName,
                                  @RequestParam("browserVersion") String browserVersion
  ) throws TestsigmaException {
    log.info(String.format("Request received for get executable path for browser - %s | version - %s | uuid - %s",
      browserName, browserVersion, uniqueId));
    Agent agent = agentService.findByUniqueId(uniqueId);
    Browsers browser = Browsers.getBrowser(browserName);
    if (browser == null) {
      throw new TestsigmaException("Browser - " + browserName + " is not supported");
    }

    PlatformBrowserVersion platformBrowserVersion =
      platformService.getPlatformBrowserVersion(agent.getOsType().getPlatform(),
        agent.getPlatformOsVersion(agent.getOsType().getPlatform()), browser,
        browserVersion, TestPlanLabType.Hybrid);

    if (platformBrowserVersion == null) {
      throw new TestsigmaException("Cant find browser with details. Browser Name - " + browserName
        + ", Browser Version - " + browserVersion + ", Platform - " + agent.getOsType().getPlatform());
    }

    return platformService.getDriverPath(platformBrowserVersion.getPlatform(),
      platformBrowserVersion.getVersion(), platformBrowserVersion.getName(),
      platformBrowserVersion.getDriverVersion());
  }

  private ArrayList<Header> getHeaders() {
    ArrayList<Header> headers = new ArrayList<>();
    headers.add(new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"));
    return headers;
  }
}
