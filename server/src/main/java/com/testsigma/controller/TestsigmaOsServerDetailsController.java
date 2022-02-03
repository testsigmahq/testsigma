package com.testsigma.controller;

import com.testsigma.config.ApplicationConfig;
import com.testsigma.dto.ServerDetailsDTO;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.os.stats.service.TestsigmaOsServerDetailsService;
import com.testsigma.service.TestsigmaOSConfigService;
import com.testsigma.util.NetworkUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/os_server_details")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestsigmaOsServerDetailsController {
  private final TestsigmaOSConfigService osConfigService;
  private final TestsigmaOsServerDetailsService serverDetailsService;
  private final ApplicationConfig applicationConfig;

  @GetMapping
  public ServerDetailsDTO get() throws TestsigmaException {
    ServerDetailsDTO serverDetails = new ServerDetailsDTO();
    serverDetails.setServerVersion(applicationConfig.getServerVersion());
    serverDetails.setServerIp(NetworkUtil.getCurrentIpAddress());
    serverDetails.setTestsigmaLabIP(serverDetailsService.getTestsigmaLabIPs());
    return serverDetails;
  }

}
