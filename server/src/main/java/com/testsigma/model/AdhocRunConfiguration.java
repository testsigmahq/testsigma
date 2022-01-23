/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "adhoc_run_configurations", uniqueConstraints = @UniqueConstraint(columnNames = "id"))
@Data
@ToString
@EqualsAndHashCode
public class AdhocRunConfiguration implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "workspace_type")
  @Enumerated(EnumType.STRING)
  private WorkspaceType workspaceType;

  @Column(name = "config_name", unique = true)
  private String configName;

  @Column(name = "type")
  private Long type;

  @Column(name = "page_timeout", length = 11)
  private Integer pageTimeOut;

  @Column(name = "element_timeout", length = 11)
  private Integer elementTimeOut;

  @Column(name = "capture_screenshots")
  private Long captureScreenshots;

  @Column(name = "desired_capabilities")
  private String desiredCapabilities;

  @Column(name = "browser")
  private String browser;

  @Column(name = "agentId")
  private Long agentId;

  @Column(name = "app_name")
  private String appName;

  @Column(name = "platform_os_version_id")
  private Long platformOsVersionId;

  @Column(name = "platform_browser_version_id")
  private Long platformBrowserVersionId;

  @Column(name = "platform_screen_resolution_id")
  private Long platformScreenResolutionId;

  @Column(name = "platform_device_id")
  private Long platformDeviceId;

  @Column(name = "udid")
  private String udId;

  @Column(name = "app_package")
  private String appPackage;

  @Column(name = "app_activity")
  private String appActivity;

  @Column(name = "app_upload_id")
  private String appUploadId;

  @Column(name = "environment_id")
  private String environmentId;

  @Column(name = "app_url")
  private String appUrl;

  @Column(name = "app_bundle_id")
  private String appBundleId;

  @Column(name = "device_id")
  private Long deviceId;

  @Column(name = "app_path_type")
  private String appPathType;

  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;

  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updatedDate;

  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "environment_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @NotFound(action = NotFoundAction.IGNORE)
  private Environment environment;

}
