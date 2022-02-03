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
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;

@Entity
@Table(name = "test_case_results")
@Data
@Log4j2
public class TestCaseResult implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "test_case_id")
  private Long testCaseId;

  @Column(name = "test_device_result_id")
  private Long environmentResultId;

  @Column(name = "suite_id")
  private Long suiteId;

  @Column(name = "iteration")
  private String iteration;

  @Column(name = "result")
  @Enumerated(EnumType.STRING)
  private ResultConstant result;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private StatusConstant status;

  @Column(name = "is_step_group")
  private Boolean isStepGroup;

  @Column(name = "message")
  private String message;

  @Column(name = "start_time")
  private Timestamp startTime;

  @Column(name = "end_time")
  private Timestamp endTime;

  @Column(name = "duration")
  private Long duration;

  @Column(name = "suite_result_id")
  private Long suiteResultId;

  @Column(name = "parent_id")
  private Long parentId;

  @Column(name = "test_data_set_name")
  private String testDataSetName;

  @Column(name = "order_id")
  private Long position;

  @Column(name = "type")
  private Long testCaseTypeId;

  @Column(name = "test_case_status")
  @Enumerated(EnumType.STRING)
  private TestCaseStatus testCaseStatus;

  @Column(name = "priority")
  private Long priorityId;

  @Column(name = "is_data_driven")
  private Boolean isDataDriven;

  @Column(name = "test_data_id")
  private Long testDataId;

  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @Column(name = "test_case_details")
  private String testCaseDetails;

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

  @Column(name = "test_plan_result_id")
  private Long testPlanResultId;

  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;

  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updatedDate;


  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "test_case_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @NotFound(action = NotFoundAction.IGNORE)
  private TestCase testCase;

  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "test_device_result_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private TestDeviceResult testDeviceResult;

  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "suite_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @NotFound(action = NotFoundAction.IGNORE)
  private TestSuite testSuite;

  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "test_plan_result_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private TestPlanResult testPlanResult;

  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "parent_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @NotFound(action = NotFoundAction.IGNORE)
  private TestCaseResult parentResult;

  @OneToOne
  @JoinColumn(name = "re_run_parent_id", insertable = false, updatable = false)
  @NotFound(action = NotFoundAction.IGNORE)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private TestCaseResult reRunParentResult;

  @OneToOne(mappedBy = "reRunParentResult")
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private TestCaseResult childResult;

  public Boolean getIsDataDriven() {
    return isDataDriven != null && isDataDriven;
  }

  public TestCaseDetails getTestCaseDetails() {
    return new ObjectMapperService().parseJson(testCaseDetails, TestCaseDetails.class);
  }

  public void setTestCaseDetails(TestCaseDetails testCaseDetails) {
    this.testCaseDetails = new ObjectMapperService().convertToJson(testCaseDetails);
  }

  public Calendar getCalendarTimeFromTimestamp(Timestamp time) {
    if (time == null) {
      return null;
    }
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(time.getTime());
    return calendar;
  }
}
