/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.controller;

import com.testsigma.config.URLConstants;
import com.testsigma.dto.SessionDTO;
import com.testsigma.mapper.AuthUserMapper;
import com.testsigma.model.AuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(path = URLConstants.SESSION_RESOURCE_URL)
@Log4j2
public class SessionsController {
  private final AuthUserMapper authUserMapper;

  @RequestMapping(method = RequestMethod.GET)
  public SessionDTO show(HttpServletRequest request) {
    SessionDTO session = new SessionDTO();
    session.setId("current");
    String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request)
      .replacePath(null)
      .build()
      .toUriString();
    session.setServerUrl(baseUrl);
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.getPrincipal() != null) {
      try {
        AuthUser authUser = (AuthUser) auth.getPrincipal();
        session.setUser(authUserMapper.map(authUser));
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }
    return session;
  }
}

