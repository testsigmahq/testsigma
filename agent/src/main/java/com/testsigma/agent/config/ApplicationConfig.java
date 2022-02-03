package com.testsigma.agent.config;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Log4j2
@Data
@Component
public class ApplicationConfig {
  @Value("${testcase.fetch.wait_interval}")
  private int testCaseFetchWaitInterval;

  @Value("${testcase.fetch.max_tries}")
  private int testCaseDefaultMaxTries;

  @Value("${docker.env}")
  private boolean dockerEnv;
}
