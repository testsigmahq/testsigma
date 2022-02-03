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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "test_cases")
@Data
@ToString
@EqualsAndHashCode
public class TestCase {

  @ManyToOne(cascade = CascadeType.PERSIST)
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "pre_requisite", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @NotFound(action = NotFoundAction.IGNORE)
  TestCase preRequisiteCase;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "start_time")
  private Timestamp startTime;
  @Column(name = "end_time")
  private Timestamp endTime;
  @Column(name = "is_data_driven", columnDefinition = "bit default 0", nullable = false)
  private Boolean isDataDriven;
  @Column(name = "is_step_group", columnDefinition = "bit default 0", nullable = false)
  private Boolean isStepGroup;
  @Column(name = "priority_id")
  private Long priority;
  @Column(name = "requirement_id")
  private Long requirementId;
  @Column(name = "description")
  private String description;
  @Column
  private String name;
  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private TestCaseStatus status;
  @Column(name = "type")
  private Long type;
  @Column(name = "test_data_id")
  private Long testDataId;
  @Column(name = "workspace_version_id")
  private Long workspaceVersionId;
  @Column(name = "pre_requisite")
  private Long preRequisite;
  @Column(name = "copied_from")
  private Long copiedFrom;
  @Column(name = "deleted")
  private Boolean deleted;
  @Column(name = "test_data_start_index")
  private Integer testDataStartIndex;
  @Column(name = "test_data_end_index")
  private Integer testDataEndIndex;
  @Column(name = "draft_at")
  private Timestamp draftAt;
  @Column(name = "obsolete_at")
  private Timestamp obsoleteAt;
  @Column(name = "ready_at")
  private Timestamp readyAt;
  @Column(name = "last_run_id")
  private Long lastRunId;
  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;
  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updatedDate;
  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "test_data_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private TestData testData;
  @OneToMany(mappedBy = "testCase", fetch = FetchType.LAZY)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<TestCaseResult> results;
  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "requirement_id", referencedColumnName = "id", insertable = false, updatable = false)
  private Requirement requirement;
  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "workspace_version_id", referencedColumnName = "id", insertable = false, updatable = false)
  private WorkspaceVersion version;
  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "priority_id", referencedColumnName = "id", insertable = false, updatable = false)
  @NotFound(action = NotFoundAction.IGNORE)
  private TestCasePriority testCasePriority;
  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "type", referencedColumnName = "id", insertable = false, updatable = false)
  @NotFound(action = NotFoundAction.IGNORE)
  private TestCaseType testCaseType;
  @OneToMany(mappedBy = "testCase", fetch = FetchType.LAZY)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<TagEntityMapping> tagUses;
  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "last_run_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private TestCaseResult lastRun;
  @OneToMany(mappedBy = "testCase", fetch = FetchType.LAZY)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<TestStep> testSteps;
  @OneToMany(mappedBy = "testCase", fetch = FetchType.LAZY)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<SuiteTestCaseMapping> suiteTestCaseMappings;
  @Transient
  private String priorityName;

  @Transient
  private String typeName;

  @Transient
  private String testDataName;

  @Transient
  private String preRequisiteName;

  @Transient
  private String order;

  @Transient
  private List<Attachment> files;

  public Boolean getIsDataDriven() {
    return isDataDriven != null && isDataDriven;
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
