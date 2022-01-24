/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.agent.controllers;

import com.testsigma.agent.config.AgentConfig;
import com.testsigma.agent.dto.AgentDTO;
import com.testsigma.agent.http.ServerURLBuilder;
import com.testsigma.agent.http.WebAppHttpClient;
import com.testsigma.agent.services.AgentService;
import com.testsigma.agent.utils.NetworkUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.testsigma.automator.http.HttpResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;

@Controller
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RootController {

  private final AgentConfig agentConfig;
  private final WebAppHttpClient httpClient;

  @RequestMapping(value = {"/"}, method = RequestMethod.GET)
  public String welcomePage(Model model) throws Exception {
    try {
      String uuid = agentConfig.getUUID();
      log.debug("Fetching agent information with UUID - " + uuid);
      String authHeader = WebAppHttpClient.BEARER + " " + this.agentConfig.getJwtApiKey();
      HttpResponse<AgentDTO> response = httpClient.get(ServerURLBuilder.agentURL(uuid), new TypeReference<>() {
      }, authHeader);
      if (response.getStatusCode() == HttpStatus.OK.value()) {
        AgentDTO agentDTO = response.getResponseEntity();
        model.addAttribute("registered", this.agentConfig.getRegistered());
        model.addAttribute("agentName", agentDTO.getTitle());
        model.addAttribute("hostName", agentDTO.getHostName());
        model.addAttribute("osType", agentDTO.getOsType().getName());
        model.addAttribute("ipAddress", agentDTO.getIpAddress());
        model.addAttribute("agentVersion", agentDTO.getAgentVersion());
      } else {
        model.addAttribute("registered", false);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw e;
    }
    return "dashboard"; //View name
  }

  @ResponseStatus(value = HttpStatus.MOVED_PERMANENTLY)
  @GetMapping(value = "/register")
  public void redirectToRegister(HttpServletResponse httpServletResponse) {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add("hostName", AgentService.getComputerName());
    queryParams.add("ip", NetworkUtil.getCurrentIpAddress());
    String registerAgentLocation = ServerURLBuilder.registerAgentURL(queryParams);
    registerAgentLocation = registerAgentLocation.replace("/#", "/ui");
    httpServletResponse.setHeader("Location", registerAgentLocation);
  }
}
