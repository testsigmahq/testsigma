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
import lombok.extern.log4j.Log4j2;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "user_preferences")
@Data
@Log4j2
public class UserPreference {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String email;

  @Column(name = "test_case_filter_id")
  private Long testCaseFilterId;

  @Column(name = "version_id")
  private Long versionId;
  @Transient
  private Long workspaceId;

  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;

  @Column(name = "updated_date")
  private Timestamp updatedDate;

}
