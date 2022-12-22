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
import lombok.extern.log4j.Log4j2;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Log4j2
@Data
@Entity
@Table(name = "test_plan_results")
@ToString(onlyExplicitlyIncluded = true)
public class TestPlanResult {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "test_plan_id")
  private Long testPlanId;

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

  @Column(name = "build_no")
  private String buildNo;

  @Column(name = "environment_id")
  private Long environmentId;

  @Column(name = "test_plan_details")
  private String testPlanDetails;

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

  @Column(name = "re_run_type")
  @Enumerated(EnumType.STRING)
  private ReRunType reRunType;

  @Column(name = "re_run_parent_id", columnDefinition = "bigint(20) DEFAULT NULL")
  private Long reRunParentId;

  @Column(name = "is_in_progress")
  private Boolean isInProgress;

  @Column(name = "triggered_type")
  @Enumerated(value = EnumType.STRING)
  private ExecutionTriggeredType triggeredType;

  @Column(name = "scheduled_id")
  private Long scheduleId;

  @Column(name = "created_date")
  @CreationTimestamp
  @ToString.Include
  private Timestamp createdDate;

  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updatedDate;

  @Transient
  private Long totalRunningCount = 0L;

  @Transient
  private Long totalQueuedCount = 0L;

  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "test_plan_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @NotFound(action = NotFoundAction.IGNORE)
  private AbstractTestPlan testPlan;

  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "test_plan_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @NotFound(action = NotFoundAction.IGNORE)
  private DryTestPlan dryTestPlan;

  @OneToMany(mappedBy = "testPlanResult", fetch = FetchType.LAZY)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<TestDeviceResult> testDeviceResults;

  @OneToMany(mappedBy = "testPlanResult", fetch = FetchType.LAZY)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<TestSuiteResult> testSuiteResults;

  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "environment_id", referencedColumnName = "id", insertable = false, updatable = false,
    nullable = true)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @NotFound(action = NotFoundAction.IGNORE)
  private Environment environment;

  @OneToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "re_run_parent_id", insertable = false, updatable = false)
  @NotFound(action = NotFoundAction.IGNORE)
  private TestPlanResult parentResult;

  @OneToOne(mappedBy = "parentResult")
  @Fetch(value = FetchMode.SELECT)
  private TestPlanResult childResult;

  @Transient
  private Long consolidatedTotalTestcasesCount = 0L;

  @Transient
  private Long consolidatedPassedCount = 0L;
  @Transient
  private Long consolidatedFailedCount = 0L;
  @Transient
  private Long consolidatedAbortedCount = 0L;
  @Transient
  private Long consolidatedStoppedCount = 0L;
  @Transient
  private Long consolidatedNotExecutedCount = 0L;
  @Transient
  private Long consolidatedPrerequisiteFailedCount = 0L;
  @Transient
  private Long consolidatedQueuedCount = 0L;
  @Transient
  private ResultConstant consolidatedResult;
  @Transient
  private String consolidatedMessage;


  public TestPlanDetails getTestPlanDetails() {
    return new ObjectMapperService().parseJson(this.testPlanDetails, TestPlanDetails.class);
  }

  public void setTestPlanDetails(TestPlanDetails testPlanDetails) {
    this.testPlanDetails = new ObjectMapperService().convertToJson(testPlanDetails);
  }
}
