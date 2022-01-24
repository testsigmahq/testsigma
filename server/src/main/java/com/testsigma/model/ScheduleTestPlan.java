/*
 * *****************************************************************************
 *  Copyright (C) 2019 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "schedule_test_plans")
@Data
public class ScheduleTestPlan implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "test_plan_id")
  private Long testPlanId;

  @Column
  private String name;

  @Column
  private String comments;

  @Column(name = "schedule_type")
  @Enumerated(EnumType.STRING)
  private ScheduleType scheduleType;

  @Column(name = "schedule_time")
  private Timestamp scheduleTime;

  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;

  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updatedDate;

  @Column
  @Enumerated(EnumType.STRING)
  private ScheduleStatus status;

  @Column(name = "queue_status")
  @Enumerated(EnumType.STRING)
  private ScheduleQueueStatus queueStatus = ScheduleQueueStatus.COMPLETED;

  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "test_plan_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private TestPlan testPlan;


}
