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
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.sql.Timestamp;


@Entity
@Table(name = "tag_entity_mapping")
@Data
public class TagEntityMapping {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;


  @Column(name = "tag_id")
  private Long tagId;


  @Column(name = "entity_id")
  private Long entityId;


  @Column(name = "type")
  @Enumerated(EnumType.STRING)
  private TagType type;

  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;

  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updatedDate;

  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "tag_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Tag tag;

  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "entity_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @NotFound(action = NotFoundAction.IGNORE)
  @WhereJoinTable(clause = "type ='TEST_CASE'")
  private TestCase testCase;

  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "entity_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @NotFound(action = NotFoundAction.IGNORE)
  @WhereJoinTable(clause = "type ='ELEMENT'")
  private Element element;

  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "entity_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @NotFound(action = NotFoundAction.IGNORE)
  @WhereJoinTable(clause = "type ='TEST_SUITE'")
  private TestSuite testSuite;

  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "entity_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @NotFound(action = NotFoundAction.IGNORE)
  @WhereJoinTable(clause = "type ='TEST_PLAN'")
  private TestPlan testPlan;

  public TagEntityMapping() {
    super();
  }
}
