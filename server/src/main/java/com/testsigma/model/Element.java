/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.model;

import com.testsigma.service.ObjectMapperService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;


@Entity
@Table(name = "elements")
@Data
@Log4j2
public class Element implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;


  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;

  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updatedDate;

  @Column(name = "workspace_version_id")
  private Long workspaceVersionId;


  @Column(name = "locator_value")
  private String locatorValue;


  @Column(name = "element_name")
  private String name;

  @Column(name = "element_type")
  private Integer type;


  @Column(name = "create_type")
  @Enumerated(EnumType.STRING)
  private ElementCreateType createdType;


  @Column(name = "locator_type")
  @Enumerated(EnumType.STRING)
  private LocatorType locatorType;

  @Column(name = "metadata")
  private String metadata;

  @Column(name = "attributes")
  private String attributes;

  @Column(name = "is_dynamic")
  private Boolean isDynamic;

  @Column(name = "screen_name_id")
  private Long screenNameId;

  @OneToMany(mappedBy = "element", fetch = FetchType.LAZY)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<TagEntityMapping> tagUses;

  @ManyToOne
  @JoinColumn(name = "screen_name_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @NotFound(action = NotFoundAction.IGNORE)
  private ElementScreenName screenNameObj;

  public ElementMetaData getMetadata() {
    return new ObjectMapperService().parseJson(metadata, ElementMetaData.class);
  }

  public void setMetadata(ElementMetaData elementMetaData) {
    this.metadata = new ObjectMapperService().convertToJson(elementMetaData);
  }
}
