/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
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
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "test_suites")
@Data
@ToString
@EqualsAndHashCode
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "entity_type", discriminatorType = DiscriminatorType.STRING)
public class AbstractTestSuite implements Serializable {

  @OneToMany(mappedBy = "testSuite", fetch = FetchType.LAZY)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  List<SuiteTestCaseMapping> testSuiteMappings;
  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "pre_requisite", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @NotFound(action = NotFoundAction.IGNORE)
  AbstractTestSuite preRequisiteSuite;
  @OneToMany(mappedBy = "testSuite", fetch = FetchType.LAZY)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  List<TestDeviceSuite> testDeviceSuites;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "name", length = 250)
  private String name;
  @Column(name = "description", length = 250)
  private String description;
  @Column(name = "action_id")
  private Long actionId;
  @Column(name = "workspace_version_id")
  private Long workspaceVersionId;
  @Column(name = "pre_requisite")
  private Long preRequisite;
  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;
  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updatedDate;
  @Column(name = "last_run_id")
  private Long lastRunId;

  @Column(name = "imported_id")
  private Long importedId;

  @Column(name = "entity_type", insertable = false, updatable = false)
  private String entityType;

  @OneToMany(mappedBy = "testSuite", fetch = FetchType.LAZY)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<TagEntityMapping> tagUses;
  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "last_run_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private TestSuiteResult lastRun;
  @Transient
  private List<String> tags;

  @Transient
  private List<Long> testCaseIds;

  @Transient
  private List<SuiteTestCaseMapping> removedTestCases;

  @Transient
  private List<SuiteTestCaseMapping> addedTestCases;

  @Transient
  private List<SuiteTestCaseMapping> updatedTestCases;
}
