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
@Table(name = "test_device_suites")
@Data
public class TestDeviceSuite {

  @Column(name = "suite_id")
  Long suiteId;
  @Column(name = "test_device_id")
  Long testDeviceId;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @MapsId("test_device_id")
  @JoinColumn(name = "test_device_id")
  private TestDevice testDevice;

  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @MapsId("suite_id")
  @JoinColumn(name = "suite_id")
  private AbstractTestSuite testSuite;

  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;

  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updatedDate;

  @Column(name = "order_id")
  private Integer position;
}
