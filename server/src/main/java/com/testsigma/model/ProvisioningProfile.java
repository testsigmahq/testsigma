/******************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 *****************************************************************************/
package com.testsigma.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.io.Serializable;
import java.net.URL;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "provisioning_profiles")
@Data
@ToString
@EqualsAndHashCode
public class ProvisioningProfile implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String name;

  @Column(name = "team_id")
  private String teamId;

  @Column
  @Enumerated(EnumType.STRING)
  private ProvisioningProfileStatus status;

  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;

  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updatedDate;

  @Transient
  private MultipartFile cer;

  @Transient
  private MultipartFile provisioningProfile;

  @Transient
  private URL csrPresignedUrl;

  @Transient
  private URL privateKeyPresignedUrl;

  @Transient
  private URL certificateCerPresignedUrl;

  @Transient
  private URL certificateCrtPresignedUrl;

  @Transient
  private URL certificatePemPresignedUrl;

  @Transient
  private URL provisioningProfilePresignedUrl;

  @Transient
  private List<String> deviceUDIDs;

  @OneToMany(mappedBy = "provisioningProfile", fetch = FetchType.LAZY)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<ProvisioningProfileUpload> provisioningProfileUploads;

  @OneToMany(mappedBy = "provisioningProfile", fetch = FetchType.LAZY)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<ProvisioningProfileDevice> provisioningProfileDevices;

}
