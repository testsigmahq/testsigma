/*
 * *****************************************************************************
 *  Copyright (C) 2019 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.service;

import com.amazonaws.HttpMethod;
import com.testsigma.config.AdditionalPropertiesConfig;
import com.testsigma.exception.JWTTokenException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.model.AgentType;
import com.testsigma.model.AuthUser;
import com.testsigma.model.AuthenticationType;
import com.testsigma.model.PreSignedAttachmentToken;
import com.testsigma.security.api.APIToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.Date;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
public class JWTTokenService {
  public static String JWT_COOKIE_NAME;
  @Setter
  private static String JWT_SECRET;
  public static String serverUuid;
  private final AdditionalPropertiesConfig additionalPropertiesConfig;
  private final ServerService serverService;

  public static String generateAuthToken(AuthUser authUser) {
    Claims claims = Jwts.claims().setSubject(authUser.getUuid());
    claims.put("uuid", authUser.getUuid());
    claims.put("email", authUser.getEmail());
    claims.put("serverUuid", authUser.getServerUuid());
    claims.put("authenticationType", authUser.getAuthenticationType());
    return Jwts.builder()
      .setClaims(claims)
      .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
      .compact();
  }

  public static String generateToken(APIToken apiToken) {
    Claims claims = Jwts.claims().setSubject(apiToken.getSubject());
    claims.put("agentType", apiToken.getAgentType());
    claims.put("serverUuid", apiToken.getServerUuid());
    claims.put("authenticationType", apiToken.getAuthenticationType());
    return Jwts.builder()
      .setClaims(claims)
      .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
      .compact();
  }


  public static APIToken parseToken(String token) {
    try {
      Claims body = Jwts.parser()
        .setSigningKey(JWT_SECRET)
        .parseClaimsJws(token)
        .getBody();

      AgentType agentType = null;
      if (body.get("agentType") != null) {
        agentType = Enum.valueOf(AgentType.class, (String) body.get("agentType"));
      }
      String serverUuid = null;
      if (body.get("serverUuid") != null) {
        serverUuid = (String) body.get("serverUuid");
      }

      APIToken apiToken = new APIToken(body.getSubject(), agentType, serverUuid);
      AuthenticationType authType = null;
      if (body.get("authenticationType") != null) {
        authType = Enum.valueOf(AuthenticationType.class, (String) body.get("authenticationType"));
      }
      apiToken.setAuthenticationType(authType);
      return apiToken;

    } catch (JwtException | ClassCastException e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  public AuthUser parseAuthToken(String token) {
    try {
      Claims body = Jwts.parser()
        .setSigningKey(additionalPropertiesConfig.getJwtSecret())
        .parseClaimsJws(token)
        .getBody();
      AuthUser authUser = new AuthUser();
      if (body.get("uuid") != null) {
        authUser.setUuid(body.get("uuid").toString());
      }
      if (body.get("email") != null) {
        authUser.setEmail(body.get("email").toString());
      }
      if (body.get("serverUuid") != null) {
        authUser.setServerUuid(body.get("serverUuid").toString());
      }
      AuthenticationType authType = null;
      if (body.get("authenticationType") != null) {
        authType = Enum.valueOf(AuthenticationType.class, (String) body.get("authenticationType"));
      }
      authUser.setAuthenticationType(authType);
      return authUser;
    } catch (JwtException | ClassCastException e) {
      return null;
    }
  }

  public String generateAttachmentToken(String fileKey, Date expiry, HttpMethod httpMethod) {
    Claims claims = Jwts.claims().setSubject(fileKey);
    claims.put("method", httpMethod);
    return Jwts.builder()
      .setClaims(claims)
      .setIssuer("Testsigma")
      .setExpiration(expiry)
      .setIssuedAt(new Date(System.currentTimeMillis()))
      .signWith(SignatureAlgorithm.HS512, additionalPropertiesConfig.getJwtSecret())
      .compact();
  }

  @PostConstruct
  public void initService() {
    JWT_SECRET = additionalPropertiesConfig.getJwtSecret();
    try {
      serverUuid = serverService.findOne().getServerUuid();
    } catch(Exception ignore) {}
  }

  public String getServerUuid() {
    if(serverUuid == null) {
      try {
        serverUuid = serverService.findOne().getServerUuid();
      } catch(Exception e) {
        log.error(e.getMessage(), e);
      }
    }
    return serverUuid;
  }

  @Value("${session.cookie_name:X-AUTH}")
  public void setJWTCookie(String privateName) {
    JWT_COOKIE_NAME = privateName;
  }

  public PreSignedAttachmentToken parseAttachmentToken(String token) {
    Claims body = Jwts.parser().setSigningKey(additionalPropertiesConfig.getJwtSecret())
      .parseClaimsJws(token)
      .getBody();
    Date tokenExpirationTime = body.getExpiration();
    Date currentTime = new Date();
    if (tokenExpirationTime != null && currentTime.after(tokenExpirationTime))
      throw new JWTTokenException("Token has expired. Please create a new PresignedURL token");
    PreSignedAttachmentToken preSignedAttachmentToken = new PreSignedAttachmentToken();
    preSignedAttachmentToken.setKey(body.getSubject());
    preSignedAttachmentToken.setExpiration(new Timestamp(tokenExpirationTime.getTime()));
    preSignedAttachmentToken.setMethod(Enum.valueOf(HttpMethod.class, (String) body.get("method")));
    return preSignedAttachmentToken;
  }
}
