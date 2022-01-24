/******************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 *****************************************************************************/
package com.testsigma.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "provisioning_profile_uploads")
@Data
@ToString
@EqualsAndHashCode
public class ProvisioningProfileUpload implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "provisioning_profile_id")
  private Long provisioningProfileId;

  @Column(name = "upload_id")
  private Long uploadId;

  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;

  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "upload_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Upload upload;

  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "provisioning_profile_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private ProvisioningProfile provisioningProfile;
}
