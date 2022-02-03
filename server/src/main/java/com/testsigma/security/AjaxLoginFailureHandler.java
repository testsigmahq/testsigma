/******************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 *****************************************************************************/
package com.testsigma.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testsigma.dto.APIErrorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AjaxLoginFailureHandler implements AuthenticationFailureHandler {

  @Autowired
  ObjectMapper objectMapper;

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                      AuthenticationException exception) throws IOException, ServletException {
    if (exception instanceof InternalAuthenticationServiceException
      || exception instanceof AuthenticationServiceException) {
      response.sendRedirect("/ui/?error=no_email");
    } else {
      APIErrorDTO errorResponse = new APIErrorDTO();
      errorResponse.setError(exception.getLocalizedMessage());
      response.setContentType("application/json;charset=UTF-8");
      response.setHeader("Cache-Control", "no-cache");
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
  }
}
