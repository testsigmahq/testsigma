/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Set;
@Entity
@Table(name = "upload_versions")
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class UploadVersion extends BaseModel {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "upload_id")
  private Long uploadId;

  @Column(name = "name")
  private String name;

  @Column(name = "file_name")
  private String fileName;

  @Column(name = "file_size")
  private Long fileSize;
  @Column
  private String path;

  @Column(name = "type")
  @Enumerated(EnumType.STRING)
  private UploadType uploadType;

  @Column(name = "last_uploaded_time")
  private Timestamp lastUploadedTime;

  @Column(name = "upload_status")
  @Enumerated(EnumType.STRING)
  private UploadStatus uploadStatus;

  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "upload_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Upload upload;


  @OneToMany(mappedBy = "upload", fetch = FetchType.LAZY)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<ProvisioningProfileUpload> provisioningProfileUploads;

  @Transient
  private Boolean signed;

  @Transient
  private String preSignedURL;

  @Transient
  private String downloadURL;

  public String getResignedAppS3PathSuffix(Long provisioningProfileId) {
    return "uploads/resigned/" + getId() + "/"
            + provisioningProfileId + "/" + getUploadFileName();
  }

  private String getUploadFileName() {
    if (StringUtils.isNotBlank(this.getFileName())) {
      return this.getFileName();
    }
    return Paths.get(this.getPath()).toFile().getName();
  }
}

