/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.dto;

import com.testsigma.model.ElementCreateType;
import com.testsigma.model.LocatorType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@EqualsAndHashCode
public class ElementDTO {
  private Long id;
  private Long workspaceVersionId;
  private String locatorValue;
  private String name;
  private Integer type;
  private ElementCreateType createdType;
  private LocatorType locatorType;
  private ElementMetaDataDTO metadata;
  private String attributes;
  private Boolean isDynamic;
  private Timestamp createdDate;
  private Timestamp updatedDate;
  private Long screenNameId;
  private ElementScreenNameDTO screenNameObj;
}
