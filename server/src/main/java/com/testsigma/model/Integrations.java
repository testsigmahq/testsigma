/*
 *****************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 ****************************************************************************
 */
package com.testsigma.model;

import com.testsigma.service.ObjectMapperService;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.Instant;

@Log4j2
@Data
@Entity
@Table(name = "integrations")
public class Integrations {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Column(name = "name")
  private String name;

  @NotNull
  @Column(name = "username")
  private String username;

  @Column(name = "description")
  private String description;

  @NotNull
  @Column(name = "workspace_id")
  private Long workspaceId;

  @NotNull
  @Column(name = "workspace")
  @Enumerated(EnumType.STRING)
  private Integration workspace;

  @NotNull
  @Column(name = "url")
  private String url;

  @NotNull
  @Column(name = "password")
  private String password;

  @Column(name = "auth_type")
  @Enumerated(EnumType.STRING)
  private IntegrationAuthType authType = IntegrationAuthType.AccessKey;

  @Column(name = "token")
  private String token;

  @Column(name = "metadata")
  private String metadata;

  @Column(name = "access_key")
  private String accessKey;

  @Column(name = "access_key_type")
  private String accessKeyType;

  @Column(name = "access_key_issued_at")
  private Instant accessKeyIssuedAt;

  @Column(name = "access_key_expires_at")
  private Instant accessKeyExpiresAt;

  @Column(name = "refresh_key")
  private String refreshKey;

  @Column(name = "refresh_key_issued_at")
  private Instant refreshKeyIssuedAt;

  @Column(name = "refresh_key_expires_at")
  private Instant refreshKeyExpiresAt;

  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;

  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updatedDate;

  public IntegrationMetaData getMetadata() {
    return new ObjectMapperService().parseJson(this.metadata, IntegrationMetaData.class);
  }

  public void setMetadata(IntegrationMetaData metadata) {
    this.metadata = new ObjectMapperService().convertToJson(metadata);
  }
}
