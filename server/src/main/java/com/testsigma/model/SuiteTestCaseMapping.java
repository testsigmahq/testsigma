/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;


@Entity
@Table(name = "test_suite_cases")
@Data
public class SuiteTestCaseMapping {

  @Column(name = "suite_id")
  Long suiteId;
  @Column(name = "test_case_id")
  Long testCaseId;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "position")
  private Integer position;

  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;

  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updatedDate;


  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @MapsId("suite_id")
  @JoinColumn(name = "suite_id")
  private AbstractTestSuite testSuite;

  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @MapsId("test_case_id")
  @JoinColumn(name = "test_case_id")
  private TestCase testCase;

  public SuiteTestCaseMapping() {
    super();
  }
}
