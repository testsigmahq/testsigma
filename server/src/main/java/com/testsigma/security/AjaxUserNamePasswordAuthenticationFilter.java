/******************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 *****************************************************************************/
package com.testsigma.security;

import com.testsigma.config.AdditionalPropertiesConfig;
import com.testsigma.config.URLConstants;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.model.AuthUser;
import com.testsigma.model.AuthenticationType;
import com.testsigma.service.CurrentUserService;
import com.testsigma.service.ObjectMapperService;
import com.testsigma.service.UserPreferenceService;
import com.testsigma.web.request.LoginRequest;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidatorFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.UUID;

@Log4j2
public class AjaxUserNamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {


  @Autowired
  UserPreferenceService userPreferenceService;

  @Autowired
  ValidatorFactory factory;

  @Autowired
  AdditionalPropertiesConfig authenticationConfig;

  @Autowired
  BCryptPasswordEncoder bCryptPasswordEncoder;

  public AjaxUserNamePasswordAuthenticationFilter() {
    super(new AntPathRequestMatcher(URLConstants.LOGIN_URL, "POST"));
  }

  @SneakyThrows
  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
    throws AuthenticationException, IOException {
    AuthUser authUser = null;
    Authentication authentication = null;
    if (AuthenticationType.FORM == authenticationConfig.getAuthenticationType()) {
      LoginRequest loginData = getPostData(request);
      UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(loginData.getUsername(),
        loginData.getPassword());
      setDetails(request, authRequest);
      request.setAttribute("TS_USER_PASSWORD_TOKEN", authRequest);
      authentication = this.getAuthenticationManager().authenticate(authRequest);
      authUser = (AuthUser) authentication.getPrincipal();
    } else if (AuthenticationType.NO_AUTH == authenticationConfig.getAuthenticationType()) {
      authUser = new AuthUser();
      authUser.setUuid(UUID.randomUUID().toString());
      authentication = new UsernamePasswordAuthenticationToken(authUser, null,
        authUser.getAuthorities());
    } else {
      throw new TestsigmaException("Invalid Authentication Type Provided" + authenticationConfig.getAuthenticationType(),
        "Invalid Authentication Type Provided" + authenticationConfig.getAuthenticationType());
    }
    CurrentUserService.setCurrentUser(authUser);
    setPreferencesEntries(authUser);
    return authentication;
  }

  private void setPreferencesEntries(AuthUser authUser) throws IOException {
    if (AuthenticationType.NO_AUTH != authenticationConfig.getAuthenticationType()) {
      try {
        userPreferenceService.insertDefaultUserPreferences(authUser);
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        throw new IOException(e.getMessage(), e);
      }
    }
  }

  @Override
  protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
    return super.requiresAuthentication(request, response);
  }

  private void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
    authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
  }

  private LoginRequest getPostData(HttpServletRequest request) throws IOException, BadCredentialsException {
    BufferedReader reader = request.getReader();
    StringBuilder sb = new StringBuilder();
    String line = reader.readLine();
    while (line != null) {
      sb.append(line + "\n");
      line = reader.readLine();
    }
    reader.close();

    return new ObjectMapperService().parseJson(sb.toString(), LoginRequest.class);
  }

}
