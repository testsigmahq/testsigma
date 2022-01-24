package com.testsigma.dto;

import com.testsigma.model.AuthenticationType;
import lombok.Data;

@Data
public class AuthenticationConfigDTO {

  private Boolean isApiEnabled;
  private AuthenticationType authenticationType;
  private String userName;
  private String password;
  private String apiKey;
  private String jwtSecret;
  private String googleClientId;
  private String googleClientSecret;

  public void setPassword(String password) {
    if (password != null)
      this.password = password.replaceAll("[\\s\\S]", "*");
    else
      this.password = null;
  }

  public void setGoogleClientSecret(String secret) {
    if (password != null)
      this.googleClientSecret = secret.replaceAll("[\\s\\S]", "*");
    else
      this.googleClientSecret = null;
  }

  public void setJwtSecret(String jwtSecret) {
    if (jwtSecret != null)
      this.jwtSecret = jwtSecret.replaceAll("[\\s\\S]", "*");
    else
      this.jwtSecret = null;
  }

}
