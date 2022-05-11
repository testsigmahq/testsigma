/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.dto.export;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.testsigma.annotation.JsonListRootName;
import com.testsigma.model.ElementCreateType;
import com.testsigma.model.LocatorType;
import lombok.Data;

import javax.persistence.Column;
import java.sql.Timestamp;

@Data
@JsonListRootName(name = "elements")
@JsonRootName(value = "element")
public class ElementXMLDTO extends BaseXMLDTO {
  @JsonProperty("id")
  private Long id;
  @JsonProperty("created-by")
  private Long createdBy;
  @JsonProperty("created-date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp createdDate;
  @JsonProperty("updated-by")
  private Long updatedBy;
  @JsonProperty("updated-date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp updatedDate;
  @JsonProperty("application-version-id")
  private Long workspaceVersionId;
  @JsonProperty("locator-value")
  private String locatorValue;
  @JsonProperty("name")
  private String name;
  @JsonProperty("type")
  private Integer type;
  @JsonProperty("created-type")
  private ElementCreateType createdType;
  @JsonProperty("locator-type")
  private LocatorType locatorType;
  @JsonProperty("attributes")
  private String attributes;
  @JsonProperty("is-dynamic")
  private Boolean isDynamic;
  @JsonProperty("screen-name-id")
  private Long screenNameId;
  @JsonProperty("reviewed-by")
  private Long reviewedBy;
  @JsonProperty("assignee")
  private Long assignee;
  @JsonProperty("review-submitted-by")
  private Long reviewSubmittedBy;
  @JsonProperty("draft-by")
  private Long draftBy;
  @JsonProperty("obsolete-at")
  private Timestamp obsoleteAt;
  @JsonProperty("obsolete-by")
  private Long obsoleteBy;
  @JsonProperty("ready-at")
  private Timestamp readyAt;
  @JsonProperty("ready-by")
  private Long readyBy;
  @JsonProperty("comments")
  private String comments;
  @JsonProperty("is_duplicated")
  private Boolean isDuplicated = false;
}
