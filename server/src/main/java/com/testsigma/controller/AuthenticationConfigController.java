package com.testsigma.controller;

import com.testsigma.config.AdditionalPropertiesConfig;
import com.testsigma.dto.AuthenticationConfigDTO;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.mapper.AuthenticationConfigMapper;
import com.testsigma.model.AuthenticationType;
import com.testsigma.service.JWTTokenService;
import com.testsigma.web.request.AuthenticationConfigRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth_config")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthenticationConfigController {
  private final AuthenticationConfigMapper mapper;
  private final AdditionalPropertiesConfig authConfig;

  @RequestMapping(method = RequestMethod.GET)
  public AuthenticationConfigDTO getConfig() {
    return mapper.map(authConfig);
  }

  @RequestMapping(method = RequestMethod.PUT, path = "")
  public AuthenticationConfigDTO update(@RequestBody AuthenticationConfigRequest request) throws TestsigmaException {

    mapper.merge(request, authConfig);
    authConfig.saveConfig();
    return mapper.map(authConfig);
  }


  @PutMapping("/regenerate/{type}")
  public void regenerateKey(@PathVariable("type") String typeString) throws TestsigmaException {
    AuthenticationType type = AuthenticationType.valueOf(typeString);
    String randomKey = String.valueOf(UUID.randomUUID()).replace("-", "");
    if (type == AuthenticationType.API) {
      authConfig.setApiKey(randomKey);
      authConfig.saveConfig();
    }
    if (type == AuthenticationType.JWT) {
      authConfig.setJwtSecret(randomKey);
      authConfig.saveConfig();
      JWTTokenService.setJWT_SECRET(randomKey);
    }
  }
}
