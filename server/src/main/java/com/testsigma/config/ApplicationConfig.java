package com.testsigma.config;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Log4j2
@Configuration
@Data
@Component
public class ApplicationConfig {

  @Value("${server.url}")
  private String serverUrl;

  @Value("${server.local.url}")
  private String serverLocalUrl;

  @Value("${server.version}")
  private String serverVersion;

  @Value("${docker.env}")
  private Boolean isDockerEnv;

  @Value("${ts.root.dir}")
  private String dataDir;

  @Value("${local.agent.url}")
  private String localAgentUrl;
}
