/******************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 *****************************************************************************/
package com.testsigma.security;

import com.testsigma.config.AdditionalPropertiesConfig;
import com.testsigma.config.URLConstants;
import com.testsigma.model.AuthUser;
import com.testsigma.model.AuthenticationType;
import com.testsigma.service.CurrentUserService;
import com.testsigma.service.JWTTokenService;
import com.testsigma.service.UserPreferenceService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Log4j2
public class JWTAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

  private final RequestMatcher sessionRequestMatcher = new AntPathRequestMatcher(URLConstants.SESSION_RESOURCE_URL,
    HttpMethod.GET.toString());
  private final RequestMatcher loginRequestMatcher = new AntPathRequestMatcher(URLConstants.LOGIN_URL,
    HttpMethod.POST.toString());
  private final RequestMatcher apiRequestMatcher = new AntPathRequestMatcher(URLConstants.API_BASE_URL + "/**");
  private final RequestMatcher presignedStorageRequestMatcher = new AntPathRequestMatcher(URLConstants.PRESIGNED_BASE_URL + "/**");
  private final RequestMatcher agentApiRequestMatcher = new AntPathRequestMatcher(URLConstants.AGENT_API_BASE_URL + "/**");
  private final RequestMatcher oauthRequestMatcher = new AntPathRequestMatcher(URLConstants.OAUTH2_BASE_URL + "/**");
  private final RequestMatcher agentCertificateMatcher = new AntPathRequestMatcher(URLConstants.AGENT_CERTIFICATE_URL + "/**");
  private final RequestMatcher serverRequestMatcher = new AntPathRequestMatcher("/servers");
  private final RequestMatcher onboardingMatcher = new AntPathRequestMatcher("/onboarding/**");
  private final RequestMatcher authConfigMatcher = new AntPathRequestMatcher("/auth_config");
  private final RequestMatcher localAgentMatcher = new AntPathRequestMatcher("/local/agents/**");


  @Autowired
  AuthenticationManager authenticationManager;
  @Autowired
  JWTTokenService jwtTokenService;
  @Autowired
  AdditionalPropertiesConfig authenticationConfig;
  @Autowired
  UserPreferenceService userPreferenceService;
  @Autowired
  JWTTokenService tokenService;


  @Value("#{new Boolean('${server.servlet.session.cookie.http-only}')}")
  private Boolean httpOnly;

  @Value("#{new Boolean('${server.servlet.session.cookie.secure}')}")
  private Boolean secure;

  public JWTAuthenticationFilter(String string) {
    super(string);
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
    throws AuthenticationException, IOException {

    Authentication auth = null;
    String jwtCookie = getJWTCookieValue(request);
    if (jwtCookie != null) {
      log.info("Identified authentication to be JWT Cookie...processing it for authentication");
      AuthUser authUser = jwtTokenService.parseAuthToken(jwtCookie);
      if((authUser != null)
        && ObjectUtils.defaultIfNull(tokenService.getServerUuid(), "").equals(authUser.getServerUuid())) {
        auth = new UsernamePasswordAuthenticationToken(authUser, null, authUser.getAuthorities());
      }
    } else if (isSessionRequest(request)) {
      log.info("Identifying sessions request.");
      auth = new UsernamePasswordAuthenticationToken(null, null, null);
    }

    if ((jwtCookie == null) && (AuthenticationType.NO_AUTH == authenticationConfig.getAuthenticationType())) {
      log.info("Identified authentication config to be NO_AUTH and cookie is absent. Setting JWT Cookie");
      AuthUser authUser = new AuthUser();
      authUser.setUuid(UUID.randomUUID().toString());
      auth = new UsernamePasswordAuthenticationToken(authUser, null, authUser.getAuthorities());
      response.setStatus(HttpServletResponse.SC_OK);
      response.setContentType("application/json;charset=UTF-8");
      response.setHeader("Cache-Control", "no-cache");
      String token = JWTTokenService.generateAuthToken(authUser);
      Cookie cookie = new Cookie(JWTTokenService.JWT_COOKIE_NAME, token);
      cookie.setSecure(this.secure);
      cookie.setHttpOnly(this.httpOnly);
      cookie.setPath("/");
      response.addCookie(cookie);
    }

    if (auth == null && !isSessionRequest(request)) {
      throw new BadCredentialsException("AUTH TOKEN MISSING");
    }

    if ((auth != null)) {
      AuthUser authUser = (AuthUser) auth.getPrincipal();
      if (authUser != null) {
        CurrentUserService.setCurrentUser(authUser);
        setPreferencesEntries(authUser);
      }
    }
    return auth;
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
    return super.requiresAuthentication(request, response) && !isLoginRequest(request)
      && !isAPIRequest(request) && !isAgentAPIRequest(request)
      && !isOAuth2LoginRequest(request) && !isAgentCertificateRequest(request)
      && !isPresignedStorageRequest(request) && !isAuthConfigRequest(request) && !isServerRequest(request) &&
      !isOnboardingRequest(request) && !isLocalAgentRequest(request);
  }

  private boolean isOnboardingRequest(HttpServletRequest request) {
    return onboardingMatcher.matches(request);
  }

  private boolean isLocalAgentRequest(HttpServletRequest request) {
    return localAgentMatcher.matches(request);
  }

  private boolean isPresignedStorageRequest(HttpServletRequest request) {
    return presignedStorageRequestMatcher.matches(request);
  }

  private boolean isSessionRequest(HttpServletRequest request) {
    return sessionRequestMatcher.matches(request);
  }

  private boolean isLoginRequest(HttpServletRequest request) {
    return loginRequestMatcher.matches(request);
  }

  private boolean isAPIRequest(HttpServletRequest request) {
    return apiRequestMatcher.matches(request);
  }

  private boolean isAgentAPIRequest(HttpServletRequest request) {
    return agentApiRequestMatcher.matches(request);
  }

  private boolean isOAuth2LoginRequest(HttpServletRequest request) {
    return oauthRequestMatcher.matches(request);
  }

  private boolean isAuthConfigRequest(HttpServletRequest request) {
    return authConfigMatcher.matches(request);
  }

  private boolean isServerRequest(HttpServletRequest request) {
    return serverRequestMatcher.matches(request);
  }

  private boolean isAgentCertificateRequest(HttpServletRequest request) {
    return agentCertificateMatcher.matches(request);
  }

  private String getJWTCookieValue(HttpServletRequest request) {
    String cookieValue = null;
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals(JWTTokenService.JWT_COOKIE_NAME)) {
          cookieValue = cookie.getValue();
        }
      }
    }
    return cookieValue;
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                          Authentication authResult) throws IOException, ServletException {

    if (authResult != null) {
      SecurityContext context = SecurityContextHolder.createEmptyContext();
      context.setAuthentication(authResult);
      SecurityContextHolder.setContext(context);
    }
    chain.doFilter(request, response);
  }

}
