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
@Table(name = "test_case_data_driven_results")
@Data
@ToString
@EqualsAndHashCode
public class TestCaseDataDrivenResult implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "test_case_id")
  private Long testCaseId;

  @Column(name = "test_data_name")
  private String testDataName;

  @Column(name = "test_data")
  private String testData;

  @Column(name = "test_device_result_id")
  private Long envRunId;

  @Column(name = "test_case_result_id")
  private Long testCaseResultId;

  @Column(name = "iteration_result_id")
  private Long iterationResultId;

  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;

  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updatedDate;

  @OneToOne
  @Fetch(FetchMode.SELECT)
  @JoinColumn(name = "iteration_result_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @NotFound(action = NotFoundAction.IGNORE)
  private TestCaseResult iterationResult;

}
