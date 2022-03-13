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
import lombok.extern.log4j.Log4j2;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "test_devices")
@Data
@EqualsAndHashCode
@ToString
@Log4j2
public class TestDevice {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "test_plan_id")
  private Long testPlanId;

  @Column(name = "title")
  private String title;

  @Column(name = "agent_id")
  private Long agentId;

  @Column(name = "device_id")
  private Long deviceId;

  @Column(name = "browser")
  private String browser;

  @Column(name = "platform_os_version_id")
  private Long platformOsVersionId;

  @Column(name = "platform_browser_version_id")
  private Long platformBrowserVersionId;

  @Column(name = "platform_screen_resolution_id")
  private Long platformScreenResolutionId;

  @Column(name = "platform_device_id")
  private Long platformDeviceId;

  @Column(name = "udid")
  private String udid;

  @Column(name = "app_upload_id")
  private Long appUploadId;

  @Column(name = "app_package")
  private String appPackage;

  @Column(name = "app_activity")
  private String appActivity;

  @Column(name = "app_url")
  private String appUrl;

  @Column(name = "app_bundle_id")
  private String appBundleId;

  @Column(name = "app_path_type")
  @Enumerated(EnumType.STRING)
  private AppPathType appPathType;

  @Column(name = "capabilities")
  private String capabilities;

  @Column(name = "disabled")
  private Boolean disable = false;

  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;

  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updatedDate;

  @Column(name = "match_browser_version")
  private Boolean matchBrowserVersion = false;

  @Column(name = "create_session_at_case_level")
  private Boolean createSessionAtCaseLevel = Boolean.FALSE;

  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "test_plan_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private AbstractTestPlan testPlan;

  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "agent_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @NotFound(action = NotFoundAction.IGNORE)
  private Agent agent;

  @OneToMany(mappedBy = "testDevice", fetch = FetchType.LAZY)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private List<TestDeviceSuite> environmentSuites;

  @OneToMany(mappedBy = "testDevice", fetch = FetchType.LAZY)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<TestDeviceResult> testDeviceResultSet;

  @Transient
  private List<Long> suiteIds;

  @Transient
  private List<TestDeviceSuite> removedSuiteIds;

  @Transient
  private List<TestDeviceSuite> addedSuiteIds;

  @Transient
  private List<TestDeviceSuite> updatedSuiteIds;

}
