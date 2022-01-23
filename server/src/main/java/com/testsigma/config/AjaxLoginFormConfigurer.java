/******************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 *****************************************************************************/
package com.testsigma.config;

import com.testsigma.security.AjaxUserNamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AjaxLoginFormConfigurer<H extends HttpSecurityBuilder<H>> extends
  AbstractAuthenticationFilterConfigurer<H,
    AjaxLoginFormConfigurer<H>,
    AjaxUserNamePasswordAuthenticationFilter> {

  public AjaxLoginFormConfigurer() {
    super(new AjaxUserNamePasswordAuthenticationFilter(), null);
  }

  public static AjaxLoginFormConfigurer<HttpSecurity> ajaxLogin() {
    return new AjaxLoginFormConfigurer<>();
  }

  @Override
  public AjaxLoginFormConfigurer<H> loginPage(String loginPage) {
    return super.loginPage(loginPage);
  }

  @Override
  public void init(H http) throws Exception {
    // START BAD CODE I know this is really bad but there was no other option left.
    Field comparatorField = ReflectionUtils.findField(HttpSecurity.class, "comparator");
    ReflectionUtils.makeAccessible(comparatorField);
    Method registerAt = ReflectionUtils.findMethod(comparatorField.getType(), "registerAt", (Class<?>[]) null);
    ReflectionUtils.makeAccessible(registerAt);
    Object comparator = ReflectionUtils.getField(comparatorField, http);
    ReflectionUtils.invokeMethod(registerAt, comparator, AjaxUserNamePasswordAuthenticationFilter.class,
      UsernamePasswordAuthenticationFilter.class);
    initDefaultLoginFilter(http);
  }

  @Override
  public void configure(H http) throws Exception {
    super.configure(http);
  }

  @Override
  protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
    return new AntPathRequestMatcher(loginProcessingUrl, "POST");
  }

  private void initDefaultLoginFilter(H http) {

  }

}
