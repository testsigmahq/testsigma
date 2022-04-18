/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
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
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "test_step_screenshots")
@EqualsAndHashCode
public class TestStepScreenshot {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "test_step_id")
  @NotNull
  private Long testStepId;

  @Column(name = "test_step_result_id")
  @NotNull
  private Long testStepResultId;

  @Column(name = "testcase_result_id")
  @NotNull
  private Long testCaseResultId;

  @Column(name = "test_device_result_id")
  @NotNull
  private Long environmentResultId;

  @Column(name = "ignored_coordinates")
  private String ignoredCoordinates;

  @Column(name = "base_image_name")
  private String baseImageName;

  @Column(name = "screen_resolution")
  private String screenResolution;

  @Column(name = "browser")
  private String browser;

  @Column(name = "browser_version")
  private Double browserVersion;

  @Column(name = "device_name")
  private String deviceName;

  @Column(name = "device_os_version")
  private String deviceOsVersion;

  @Column(name = "test_data_id")
  private Long testDataId;

  @Column(name = "test_data_set_name")
  private String testDataSetName;

  @Column(name = "base_image_size")
  private String baseImageSize;

  @Column(name = "entity_type")
  private String entityType;

  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;

  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updatedDate;

  @Transient
  private String screenShotURL;

  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "test_step_result_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @NotFound(action = NotFoundAction.IGNORE)
  private TestStepResult testStepResult;

}
