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
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "workspaces")
@Data
public class Workspace {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", length = 250, unique = true)
  private String name;

  @Column(name = "is_demo")
  private Boolean isDemo;

  @Column(name = "description", length = 250)
  private String description;

  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;

  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updatedDate;

  @Column(name = "type")
  @Enumerated(EnumType.STRING)
  private WorkspaceType workspaceType;

  @OneToMany(mappedBy = "workspace", fetch = FetchType.LAZY)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @JsonIgnore
  private Set<WorkspaceVersion> workspaceVersions;
}
