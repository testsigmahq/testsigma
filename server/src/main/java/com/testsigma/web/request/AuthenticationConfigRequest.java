package com.testsigma.web.request;

import com.testsigma.model.AuthenticationType;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class AuthenticationConfigRequest {

  private Boolean isApiEnabled;
  private AuthenticationType authenticationType;
  private String userName;
  private String password;
  private String apiKey;
  private String jwtSecret;
  private String googleClientId;
  private String googleClientSecret;


  public String getPassword() {
    if (this.password != null) {
      if (StringUtils.countMatches(this.password, "*") == this.password.length())
        return null;
    }
    return this.password;
  }

  public String getJwtSecret() {
    if (this.jwtSecret != null) {
      if (StringUtils.countMatches(this.jwtSecret, "*") == this.jwtSecret.length())
        return null;
    }
    return this.password;
  }

  public String getGoogleClientSecret() {
    if (this.googleClientSecret != null) {
      if (StringUtils.countMatches(this.googleClientSecret, "*") == this.googleClientSecret.length())
        return null;
    }
    return this.googleClientSecret;
  }
}

