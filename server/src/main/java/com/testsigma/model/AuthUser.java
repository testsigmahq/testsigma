package com.testsigma.model;

import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
public class AuthUser implements UserDetails, OidcUser {
  private String uuid;
  private String email;
  private String userName;
  private String password;
  private String serverUuid;
  private Map<String, Object> claims;
  private OidcUserInfo userInfo;
  private OidcIdToken IdToken;
  private AuthenticationType authenticationType;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return null;
  }

//  public String getPassword() {
//    return "$2a$10$MrbigLW8h21S9fOANK1xBeDRKAVw4AvdHKqSn49qi75dYTaYXSQge";
//  }

  @Override
  public String getUsername() {
    return userName;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public Map<String, Object> getAttributes() {
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("email", email);
    return attributes;
  }

  @Override
  public String getName() {
    return ObjectUtils.defaultIfNull(uuid, UUID.randomUUID().toString());
  }
}
