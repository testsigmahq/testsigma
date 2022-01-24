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
public class AuthenticationConfigProperties {

  @Value("${authentication.google.clientId}")
  private String googleOAuthClientID;
  @Value("${authentication.google.clientSecret}")
  private String googleOAuthClientSecret;
}
