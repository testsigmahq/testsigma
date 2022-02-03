package com.testsigma.security;

import com.testsigma.config.URLConstants;
import com.testsigma.exception.JWTTokenException;
import com.testsigma.model.AuthUser;
import com.testsigma.model.AuthenticationType;
import com.testsigma.model.PreSignedAttachmentToken;
import com.testsigma.service.CurrentUserService;
import com.testsigma.service.JWTTokenService;
import com.testsigma.service.OnPremiseStorageService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
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

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Log4j2
public class PresignedAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

  @Autowired
  JWTTokenService jwtTokenService;


  public PresignedAuthenticationFilter() {
    super(URLConstants.PRESIGNED_BASE_URL + "/**");
  }

  @Override
  protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
    return super.requiresAuthentication(request, response);
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
    throws AuthenticationException {
    String token = request.getParameter(OnPremiseStorageService.STORAGE_SIGNATURE);

    log.info("Presigned URL token - " + token);
    if (StringUtils.isBlank(token)) {
      throw new BadCredentialsException("No JWT token found in request");
    }
    PreSignedAttachmentToken attachmentToken = jwtTokenService.parseAttachmentToken(token);
    if (!attachmentToken.getMethod().toString().equals(request.getMethod()))
      throw new JWTTokenException("Token Corrupted");
    AuthUser authUser = new AuthUser();
    authUser.setUuid(UUID.randomUUID().toString());
    authUser.setUserName(token);
    authUser.setAuthenticationType(AuthenticationType.JWT);
    Authentication auth = new UsernamePasswordAuthenticationToken(authUser, null, authUser.getAuthorities());
    CurrentUserService.setCurrentUser(authUser);
    return auth;
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                          Authentication authResult)
    throws IOException, ServletException {
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authResult);
    SecurityContextHolder.setContext(context);
    CurrentUserService.setCurrentUser((AuthUser) authResult.getPrincipal());

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
