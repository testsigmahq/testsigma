package com.testsigma.service;

import com.testsigma.config.AdditionalPropertiesConfig;
import com.testsigma.model.AuthUser;
import com.testsigma.model.AuthenticationType;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthUserService implements UserDetailsService, OAuth2UserService<OidcUserRequest, OidcUser> {

  private final AdditionalPropertiesConfig authenticationConfig;
  @Setter
  private BCryptPasswordEncoder bCryptPasswordEncoder;
  private final ServerService serverService;

  @Override
  public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
    AuthUser authUser = new AuthUser();
    authUser.setUuid(UUID.randomUUID().toString());
    setServerUuid(authUser);
    switch (authenticationConfig.getAuthenticationType()) {
      case FORM:
        authUser.setEmail(authenticationConfig.getUserName());
        authUser.setUserName(authenticationConfig.getUserName());
        authUser.setPassword(bCryptPasswordEncoder.encode(authenticationConfig.getPassword()));
        authUser.setAuthenticationType(AuthenticationType.FORM);
        if (!authUser.getUsername().equals(name)) {
          throw new UsernameNotFoundException("Unable to find user with name - " + name);
        }
        break;
      case API:
        authUser.setAuthenticationType(AuthenticationType.API);
        break;
      case NO_AUTH:
        authUser.setAuthenticationType(AuthenticationType.NO_AUTH);
        break;
      case JWT:
        authUser.setAuthenticationType(AuthenticationType.JWT);
        break;
      case OIDC:
        authUser.setAuthenticationType(AuthenticationType.OIDC);
        break;
      default:
        throw new UsernameNotFoundException("Unable to find user with name - " + name);
    }
    return authUser;
  }

  @Override
  public OidcUser loadUser(OidcUserRequest oidcUserRequest) throws OAuth2AuthenticationException {
    OidcUser oidcUser = new OidcUserService().loadUser(oidcUserRequest);
    try {
      AuthUser authUser = new AuthUser();
      setServerUuid(authUser);
      String email = oidcUser.getAttributes().get("email").toString();
      if (StringUtils.isEmpty(email))
        throw new UsernameNotFoundException("Unable to find user - " + email);
      authUser.setEmail(oidcUser.getEmail());
      authUser.setUserName(oidcUser.getFullName());
      authUser.setClaims(oidcUser.getClaims());
      authUser.setUserInfo(oidcUser.getUserInfo());
      authUser.setIdToken(oidcUser.getIdToken());
      authUser.setAuthenticationType(AuthenticationType.OIDC);
      authUser.setUuid(UUID.randomUUID().toString());
      return authUser;
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
      throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
    }
  }

  private void setServerUuid(AuthUser authUser) {
    try {
      if (StringUtils.isEmpty(authUser.getServerUuid())) {
        authUser.setServerUuid(serverService.findOne().getServerUuid());
      }
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
    }
  }
}
