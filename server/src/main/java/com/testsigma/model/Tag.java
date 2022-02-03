/*
 * *****************************************************************************
 *  Copyright (C) 2019 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;


@Entity
@Table(name = "tags",
  uniqueConstraints = @UniqueConstraint(columnNames = "id"))
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Tag {


  @Column(name = "created_date")
  @CreationTimestamp
  protected Timestamp createdDate = null;

  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updatedDate;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, unique = true)
  private Long id;


  @Column(name = "name", length = 2000)
  private String name;


  @Column(name = "type")
  @Enumerated(EnumType.STRING)
  private TagType type;

  @Column(name = "count")
  private Integer count;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<TagEntityMapping> tagUses;

}
