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
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "uploads")
@Data
@ToString
@EqualsAndHashCode
public class Upload {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "workspace_id")
  private Long workspaceId;

  @Column(name = "name")
  private String name;

  @Column(name = "path")
  private String appPath;

  @Column(name = "file_name")
  private String fileName;

  @Column(name = "type")
  @Enumerated(EnumType.STRING)
  private UploadType type;

  @Column(name = "version")
  private String version;

  @Column(name = "file_size")
  private Integer fileSize;

  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;

  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updatedDate;

  @Column(name = "upload_status")
  @Enumerated(EnumType.STRING)
  private UploadStatus uploadStatus;

  @Transient
  private String preSignedURL;

  @OneToMany(mappedBy = "upload", fetch = FetchType.LAZY)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<ProvisioningProfileUpload> provisioningProfileUploads;


  public String getResignedAppS3PathSuffix(Long provisioningProfileId) {
    return "uploads/resigned/" + getId() + "/"
      + provisioningProfileId + "/" + getUploadFileName();
  }

  private String getUploadFileName() {
    if (StringUtils.isNotBlank(this.getFileName())) {
      return this.getFileName();
    }
    return Paths.get(this.getAppPath()).toFile().getName();
  }

}
