/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.dto.export;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.testsigma.annotation.JsonListRootName;
import com.testsigma.model.ElementCreateType;
import com.testsigma.model.ElementMetaData;
import com.testsigma.model.LocatorType;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.sql.Timestamp;

@Data
@Log4j2
@JsonListRootName(name = "Elements")
@JsonRootName(value = "Element")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ElementCloudXMLDTO extends BaseXMLDTO {
  @JsonProperty("Id")
  private Long id;
  @JsonProperty("CreatedBy")
  private Long createdBy;
  @JsonProperty("CreatedDate")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp createdDate;
  @JsonProperty("UpdatedBy")
  private Long updatedBy;
  @JsonProperty("UpdatedDate")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp updatedDate;
  @JsonProperty("ApplicationVersionId")
  private Long workspaceVersionId;
  @JsonProperty("Definition")
  private String locatorValue;
  @JsonProperty("Name")
  private String name;
  @JsonProperty("Type")
  private Integer type;
  @JsonProperty("CreatedType")
  private ElementCreateType createdType;
  @JsonProperty("LocatorType")
  private LocatorType locatorType;
  @JsonProperty("IsAdvanced")
  private Boolean isAdvanced;
  @JsonProperty("Metadata")
  private String metadata;
  @JsonProperty("Attributes")
  private String attributes;
  @JsonProperty("IsDynamic")
  private Boolean isDynamic;
  @JsonProperty("ScreenNameId")
  private Long screenNameId;
  @JsonProperty("ReviewedBy")
  private Long reviewedBy;
  @JsonProperty("Assignee")
  private Long assignee;
  @JsonProperty("ReviewSubmittedBy")
  private Long reviewSubmittedBy;
  @JsonProperty("DraftBy")
  private Long draftBy;
  @JsonProperty("ObsoleteAt")
  private Timestamp obsoleteAt;
  @JsonProperty("ObsoleteBy")
  private Long obsoleteBy;
  @JsonProperty("ReadyAt")
  private Timestamp readyAt;
  @JsonProperty("ReadyBy")
  private Long readyBy;
  @JsonProperty("Comments")
  private String comments;
  @JsonProperty("AutoHealingEnabled")
  private Boolean autoHealingEnabled;
  @JsonProperty("FieldName")
  private String fieldName;

  public ElementMetaData getMetadata() {
    try {
      if (metadata != null)
        return new ObjectMapper()
          .setSerializationInclusion(JsonInclude.Include.NON_NULL)
          .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
          .readValue(metadata, ElementMetaData.class);
    } catch (JsonProcessingException exception) {
      log.error(exception, exception);
    }
    return null;
  }

  public void setMetadata(ElementMetaData uiIdentifierMetaData) {
    try {
      if (uiIdentifierMetaData != null)
        this.metadata = new ObjectMapper()
          .setSerializationInclusion(JsonInclude.Include.NON_NULL)
          .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
          .writeValueAsString(uiIdentifierMetaData);
    } catch (Exception e) {
      log.error(e, e);
    }
  }
}
