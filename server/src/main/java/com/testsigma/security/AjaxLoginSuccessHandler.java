/******************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 *****************************************************************************/
package com.testsigma.security;


import com.testsigma.model.AuthUser;
import com.testsigma.model.AuthenticationType;
import com.testsigma.service.CurrentUserService;
import com.testsigma.service.JWTTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AjaxLoginSuccessHandler implements AuthenticationSuccessHandler {

  @Value("#{new Boolean('${server.servlet.session.cookie.http-only}')}")
  private Boolean httpOnly;

  @Value("#{new Boolean('${server.servlet.session.cookie.secure}')}")
  private Boolean secure;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType("application/json;charset=UTF-8");
    response.setHeader("Cache-Control", "no-cache");
    AuthUser authUser = (AuthUser) authentication.getPrincipal();
    CurrentUserService.setCurrentUser(authUser);
    String token = JWTTokenService.generateAuthToken(authUser);
    Cookie cookie = new Cookie(JWTTokenService.JWT_COOKIE_NAME, token);
    cookie.setSecure(secure);
    cookie.setHttpOnly(httpOnly);
    cookie.setPath("/");
    response.addCookie(cookie);
    if (canSendRedirect(authentication))
      response.sendRedirect("/");
  }

  private boolean canSendRedirect(Authentication authentication) {
    AuthUser authUser = (AuthUser) authentication.getPrincipal();
    return AuthenticationType.OIDC.equals(authUser.getAuthenticationType());// ||
  }

}
