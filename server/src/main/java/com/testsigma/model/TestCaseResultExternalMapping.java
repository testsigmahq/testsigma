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
import java.sql.Timestamp;
import java.util.Map;

@Entity
@Table(name = "test_case_result_external_mappings", uniqueConstraints = @UniqueConstraint(columnNames = "id"))
@Data
public class TestCaseResultExternalMapping {

  @Transient
  Map<String, Object> fields;
  @Transient
  Boolean linkToExisting = false;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "test_case_result_id")
  private Long testCaseResultId;
  @Column(name = "workspace_id")
  private Long workspaceId;
  @Column(name = "external_id")
  private String externalId;
  @Column
  private String misc;
  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;
  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updatedDate;
  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "test_case_result_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @NotFound(action = NotFoundAction.IGNORE)
  private TestCaseResult testCaseResult;
  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "workspace_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @NotFound(action = NotFoundAction.IGNORE)
  private Integrations workspace;

}
