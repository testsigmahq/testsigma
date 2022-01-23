package com.testsigma.http;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class WebAppHttpClient extends AutomatorHttpClient {
  @Autowired
  public WebAppHttpClient() {
    super();
  }
}
