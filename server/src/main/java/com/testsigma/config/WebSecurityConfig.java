/******************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 *****************************************************************************/
package com.testsigma.config;


import com.testsigma.security.AjaxLoginFailureHandler;
import com.testsigma.security.AjaxLoginSuccessHandler;
import com.testsigma.security.JWTAuthenticationFilter;
import com.testsigma.security.api.AgentJwtAuthenticationFilter;
import com.testsigma.security.api.RestAuthenticationEntryPoint;
import com.testsigma.service.AuthUserService;
import com.testsigma.service.JWTTokenService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.validation.constraints.NotNull;

import static com.testsigma.config.AjaxLoginFormConfigurer.ajaxLogin;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
  private final static String JSESSIONID_COOKIE = "JSESSIONID";
  private final AuthUserService authUserService;
  private final AuthenticationConfigProperties authenticationConfigProperties;
  private final AdditionalPropertiesConfig additionalPropertiesConfig;
  @Value("${testsigma.csrf.header:X-C}")
  String headerName;

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    BCryptPasswordEncoder bCryptPasswordEncoder = bCryptPasswordEncoder();
    auth.userDetailsService(authUserService).passwordEncoder(bCryptPasswordEncoder);
    authUserService.setBCryptPasswordEncoder(bCryptPasswordEncoder);
  }

  @Bean
  public AuthenticationEntryPoint authenticationEntryPoint() {
    return new RestAuthenticationEntryPoint();
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @NotNull
  @Bean
  public AjaxLoginSuccessHandler ajaxLoginSuccessHandler() {
    return new AjaxLoginSuccessHandler();
  }

  @NotNull
  @Bean
  public AjaxLoginFailureHandler ajaxLoginFailureHandler() {
    return new AjaxLoginFailureHandler();
  }

  @Bean
  public JWTAuthenticationFilter jwtAuthenticationFilter() throws Exception {
    JWTAuthenticationFilter filter = new JWTAuthenticationFilter("/**/*");
    filter.setAuthenticationManager(super.authenticationManagerBean());
    return filter;
  }

  @Bean
  public com.testsigma.security.api.APIAuthenticationFilter apiJwtAuthenticationFilter() throws Exception {
    com.testsigma.security.api.APIAuthenticationFilter filter = new com.testsigma.security.api.APIAuthenticationFilter();
    filter.setAuthenticationManager(super.authenticationManagerBean());
    return filter;
  }

  @Bean
  public com.testsigma.security.PresignedAuthenticationFilter presignedJwtAuthenticationFilter() throws Exception {
    com.testsigma.security.PresignedAuthenticationFilter filter = new com.testsigma.security.PresignedAuthenticationFilter();
    filter.setAuthenticationManager(super.authenticationManagerBean());
    return filter;
  }

  @Bean
  public AgentJwtAuthenticationFilter agentJwtAuthorizationFilter() throws Exception {
    AgentJwtAuthenticationFilter filter = new AgentJwtAuthenticationFilter();
    filter.setAuthenticationManager(super.authenticationManagerBean());
    return filter;
  }

  @Bean
  public AuthorizationRequestRepository<OAuth2AuthorizationRequest> cookieAuthorizationRequestRepository() {
    return new com.testsigma.security.HttpCookieOAuth2AuthorizationRequestRepository();
  }

  @Bean
  public ClientRegistrationRepository clientRegistrationRepository() {
    return new InMemoryClientRegistrationRepository(this.googleClientRegistration());
  }

  private ClientRegistration googleClientRegistration() {

    String googleClientId = StringUtils.defaultIfEmpty(additionalPropertiesConfig.getGoogleClientId(),
      authenticationConfigProperties.getGoogleOAuthClientID());
    String googleClientSecret = StringUtils.defaultIfEmpty(additionalPropertiesConfig.getGoogleClientSecret(),
      authenticationConfigProperties.getGoogleOAuthClientSecret());

    return CommonOAuth2Provider.GOOGLE.getBuilder("google")
      .clientId(googleClientId)
      .clientSecret(googleClientSecret)
      .build();
  }


  @Override
  public void configure(WebSecurity web) {
    web.ignoring()
      .antMatchers(HttpMethod.GET, URLConstants.SESSION_RESOURCE_URL)
      .antMatchers((URLConstants.AGENT_CERTIFICATE_URL + URLConstants.ALL_SUB_URLS))
      .antMatchers(URLConstants.ASSETS_URL)
      .antMatchers("/servers")
      .antMatchers("/auth_config")
      .antMatchers("/onboarding/**")
      .antMatchers("/local/agents/**");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    configureOauth2LoginHandlers(
      configureFilters(
        configureLoginHandlers(
          configureLogoutHandlers(
            configureExceptionHandling(
              configureUrlAuthorizations(
                configureCsrf(
                  configureCors(
                    basicConfig(http)
                  )
                )
              )
            )
          )
        )
      )
    );
  }

  private HttpSecurity basicConfig(HttpSecurity http) throws Exception {
    return http.headers().frameOptions().disable().and()
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and();
  }

  private HttpSecurity configureCors(HttpSecurity http) throws Exception {
    return http.cors().and();
  }

  private HttpSecurity configureCsrf(HttpSecurity http) throws Exception {
    return http.csrf().disable();
  }

  private HttpSecurity configureUrlAuthorizations(HttpSecurity http) throws Exception {
    return http.authorizeRequests().antMatchers(URLConstants.ASSETS_URL).permitAll()
      .antMatchers(URLConstants.AGENT_CERTIFICATE_URL + URLConstants.ALL_SUB_URLS).permitAll()
      .antMatchers(HttpMethod.POST, URLConstants.LOGIN_URL).permitAll()
      .antMatchers(HttpMethod.GET, URLConstants.SESSION_RESOURCE_URL).permitAll()
      .antMatchers(URLConstants.ALL_URLS).access("isFullyAuthenticated()")
      .antMatchers(URLConstants.ALL_URLS).authenticated().and();
  }

  private HttpSecurity configureExceptionHandling(HttpSecurity http) throws Exception {
    return http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint()).and();
  }

  private HttpSecurity configureLogoutHandlers(HttpSecurity http) throws Exception {
    return http.logout()
      .logoutRequestMatcher(new AntPathRequestMatcher(URLConstants.LOGOUT_URL, HttpMethod.GET.name()))
      .logoutSuccessHandler((new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK)))
      .deleteCookies(JSESSIONID_COOKIE)
      .deleteCookies(JWTTokenService.JWT_COOKIE_NAME).invalidateHttpSession(true).and();
  }

  private HttpSecurity configureLoginHandlers(HttpSecurity http) throws Exception {
    return http.anonymous().disable().apply(ajaxLogin()).loginPage(URLConstants.LOGIN_URL)
      .successHandler(ajaxLoginSuccessHandler()).failureHandler(ajaxLoginFailureHandler()).and();
  }

  private HttpSecurity configureFilters(HttpSecurity http) throws Exception {
    return http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
      .addFilterAfter(apiJwtAuthenticationFilter(), JWTAuthenticationFilter.class)
      .addFilterAfter(agentJwtAuthorizationFilter(), JWTAuthenticationFilter.class)
      .addFilterBefore(presignedJwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
  }

  private HttpSecurity configureOauth2LoginHandlers(HttpSecurity http) throws Exception {

    return http.oauth2Login().redirectionEndpoint()
      .and().authorizationEndpoint()
      .authorizationRequestRepository(cookieAuthorizationRequestRepository()).and()
      .userInfoEndpoint()
      .oidcUserService(authUserService).and()
      .clientRegistrationRepository(clientRegistrationRepository())
      .successHandler(ajaxLoginSuccessHandler())
      .failureHandler(ajaxLoginFailureHandler()).and();
  }
}
