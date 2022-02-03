/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.model;

import com.testsigma.service.ObjectMapperService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "test_device_results")
public class TestDeviceResult implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "test_device_id")
  private Long testDeviceId;

  @Column(name = "start_time")
  private Timestamp startTime;

  @Column(name = "end_time")
  private Timestamp endTime;

  @Column(name = "duration")
  private Long duration;

  @Column(name = "result")
  @Enumerated(EnumType.STRING)
  private ResultConstant result;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private StatusConstant status;

  @Column(name = "message")
  private String message;

  @Column(name = "test_plan_result_id")
  private Long testPlanResultId;

  @Column(name = "browser_version")
  private String browserVersion;

  @Column(name = "environment_settings")
  private String testDeviceSettings;

  @Column(name = "execution_initiated_on")
  private Timestamp executionInitiatedOn;

  @Column(name = "agent_picked_on")
  private Timestamp agentPickedOn;

  @Column(name = "device_allocated_on")
  private Timestamp deviceAllocatedOn;

  @Column(name = "session_created_on")
  private Timestamp sessionCreatedOn;

  @Column(name = "session_completed_on")
  private Timestamp sessionCompletedOn;

  @Column(name = "total_count", columnDefinition = "bigint(20) DEFAULT 0")
  private Long totalCount = 0L;

  @Column(name = "failed_count", columnDefinition = "bigint(20) DEFAULT 0")
  private Long failedCount = 0L;

  @Column(name = "passed_count", columnDefinition = "bigint(20) DEFAULT 0")
  private Long passedCount = 0L;

  @Column(name = "aborted_count", columnDefinition = "bigint(20) DEFAULT 0")
  private Long abortedCount = 0L;

  @Column(name = "stopped_count", columnDefinition = "bigint(20) DEFAULT 0")
  private Long stoppedCount = 0L;

  @Column(name = "not_executed_count", columnDefinition = "bigint(20) DEFAULT 0")
  private Long notExecutedCount = 0L;

  @Column(name = "queued_count", columnDefinition = "bigint(20) DEFAULT 0")
  private Long queuedCount = 0L;

  @Column(name = "is_visually_passed")
  private Boolean isVisuallyPassed;

  @Column(name = "session_id")
  private String sessionId;

  @Column(name = "re_run_parent_id")
  private Long reRunParentId;

  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;

  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updatedDate;

  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "test_plan_result_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private TestPlanResult testPlanResult;

  @OneToMany(mappedBy = "testDeviceResult", fetch = FetchType.LAZY)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<TestSuiteResult> testSuiteResults;

  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "test_device_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @NotFound(action = NotFoundAction.IGNORE)
  private TestDevice testDevice;

  @OneToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "re_run_parent_id", insertable = false, updatable = false)
  @NotFound(action = NotFoundAction.IGNORE)
  private TestDeviceResult parentResult;

  @OneToOne(mappedBy = "parentResult")
  @Fetch(value = FetchMode.SELECT)
  private TestDeviceResult childResult;
  @Transient
  private List<TestSuiteResult> suiteResults;

  public TestDeviceSettings getTestDeviceSettings() {
    return new ObjectMapperService().parseJson(testDeviceSettings, TestDeviceSettings.class);
  }

  public void setTestDeviceSettings(TestDeviceSettings testDeviceSettings) {
    this.testDeviceSettings = new ObjectMapperService().convertToJson(testDeviceSettings);
  }
}
