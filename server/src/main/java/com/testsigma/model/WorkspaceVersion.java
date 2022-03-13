/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "workspace_versions")
@Data
public class WorkspaceVersion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;


  @Column(name = "workspace_id", length = 250)
  private Long workspaceId;


  @Column(name = "description", length = 250)
  private String description;


  @Column(name = "version_name", length = 250)
  private String versionName;


  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;


  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updatedDate;

  @Transient
  private Map<String, String> files;

  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "workspace_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Workspace workspace;



  @OneToMany(mappedBy = "workspaceVersion", fetch = FetchType.LAZY)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @JsonIgnore
  private Set<AbstractTestPlan> testPlans;


}
