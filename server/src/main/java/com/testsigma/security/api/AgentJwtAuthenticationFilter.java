/*
 * *****************************************************************************
 *  Copyright (C) 2019 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.security.api;

import com.testsigma.config.URLConstants;
import com.testsigma.exception.JwtTokenMissingException;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.model.Agent;
import com.testsigma.model.AuthUser;
import com.testsigma.model.AuthenticationType;
import com.testsigma.service.AgentService;
import com.testsigma.service.CurrentUserService;
import com.testsigma.service.JWTTokenService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
public class AgentJwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

  private final RequestMatcher agentCertificateMatcher = new AntPathRequestMatcher(URLConstants.AGENT_CERTIFICATE_URL + "/**");
  @Autowired
  AgentService agentService;
  @Autowired
  JWTTokenService jwtTokenService;

  public AgentJwtAuthenticationFilter() {
    super(URLConstants.AGENT_API_BASE_URL + "/**");
  }

  @Override
  protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
    return super.requiresAuthentication(request, response) && !agentCertificateMatcher.matches(request);
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
    throws AuthenticationException {

    Authentication auth = null;

    String header = request.getHeader("Authorization");

    if (header == null || !header.startsWith("Bearer ")) {
      throw new JwtTokenMissingException("No JWT token found in request headers");
    }

    String authToken = header.substring(7);
    try {
      APIToken apiToken = JWTTokenService.parseToken(authToken);
      if (apiToken == null)
        throw new BadCredentialsException("TOKEN MISSING");
      log.info("APIToken retrieved from headers - " + apiToken);
      Agent agent = agentService.findByUniqueId(apiToken.getSubject());
      log.info("Agent records retrieved from API Token - " + agent);
      AuthUser authUser = new AuthUser();
      authUser.setUuid(apiToken.getSubject());
      authUser.setAuthenticationType(AuthenticationType.API);
      auth = new UsernamePasswordAuthenticationToken(authUser, null, authUser.getAuthorities());
      CurrentUserService.setCurrentUser(authUser);
    } catch (ResourceNotFoundException e) {
      if (request.getRequestURI() != null && !request.getRequestURI().equals("/agent/register")) {
        response.setStatus(HttpStatus.PRECONDITION_FAILED.value());
        return null;
      }
    }
    return auth;
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                          Authentication authResult)
    throws IOException, ServletException {
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authResult);
    SecurityContextHolder.setContext(context);

    // As this authentication is in HTTP header, after success we need to continue the request normally
    // and return the response as if the resource was not secured at all
    chain.doFilter(request, response);
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            AuthenticationException failed) throws IOException, ServletException {
    SecurityContextHolder.clearContext();
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.getWriter().write(failed.getMessage());
  }
}
