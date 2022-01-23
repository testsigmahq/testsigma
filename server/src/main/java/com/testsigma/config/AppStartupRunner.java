package com.testsigma.config;

import com.testsigma.tasks.StandaloneAppBridge;
import com.testsigma.service.ServerService;
import com.testsigma.service.TestsigmaOSConfigService;
import com.testsigma.automator.AutomatorConfig;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Log4j2
public class AppStartupRunner implements ApplicationRunner {

  @Autowired
  WebApplicationContext webApplicationContext;

  @Autowired
  private ServerService serverService;

  @Autowired
  private ApplicationConfig applicationConfig;

  @Autowired
  private TestsigmaOSConfigService testsigmaOSConfigService;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    AutomatorConfig automatorConfig = AutomatorConfig.getInstance();
    automatorConfig.setAppBridge(webApplicationContext.getBean(StandaloneAppBridge.class));
    automatorConfig.init();
    log.info("-------------------- APP STARTED -------------------- ");
    log.info("Testsigma Cloud Server: " + testsigmaOSConfigService.getUrl());
    log.info(">>>>>>>>>>>>>>>>>>>>> Open url " + applicationConfig.getServerUrl()
      + " to access server  <<<<<<<<<<<<<<<<<");
    log.info("Application Configuration: " + applicationConfig);
    serverService.syncServer();
  }
}
