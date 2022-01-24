/******************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 *****************************************************************************/
package com.testsigma.web.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.testsigma.model.IntegrationMetaData;
import com.testsigma.model.Integration;
import com.testsigma.model.IntegrationAuthType;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.Instant;

@Data
public class IntegrationsRequest {
  private String id;

  private String name;

  @JsonProperty("username")
  private String username;

  private String description;

  @JsonProperty("workspace")
  private Integration workspace;

  private Long workspaceId;

  private String url;

  private String password;

  @JsonProperty("auth_type")
  @Enumerated(EnumType.STRING)
  private IntegrationAuthType authType = IntegrationAuthType.AccessKey;

  private String token;

  private IntegrationMetaData metadata;

  @JsonProperty("access_key")
  private String accessKey;

  @JsonProperty("access_key_type")
  private String accessKeyType;

  @JsonProperty("access_key_issued_at")
  private Instant accessKeyIssuedAt;

  @JsonProperty("access_key_expires_at")
  private Instant accessKeyExpiresAt;

  @JsonProperty("refresh_key")
  private String refreshKey;

  @JsonProperty("refresh_key_issued_at")
  private Instant refreshKeyIssuedAt;

  @JsonProperty("refresh_key_expires_at")
  private Instant refreshKeyExpiresAt;
}
