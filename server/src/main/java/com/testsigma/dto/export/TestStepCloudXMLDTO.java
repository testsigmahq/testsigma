/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.dto.export;

import com.fasterxml.jackson.annotation.*;
import com.testsigma.annotation.JsonListRootName;
import com.testsigma.model.*;
import com.testsigma.service.ObjectMapperService;
import lombok.Data;

import java.sql.Timestamp;


@Data
@JsonListRootName(name = "TestSteps")
@JsonRootName(value = "TestStep")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestStepCloudXMLDTO extends BaseXMLDTO {
  @JsonProperty("Id")
  private Long id;
  @JsonProperty("StepDescription")
  private String stepDescription;
  @JsonProperty("Priority")
  private TestStepPriority priority;
  @JsonProperty("Position")
  private Integer position;
  @JsonProperty("PreRequisiteStepId")
  private Long preRequisiteStepId;
  @JsonProperty("Action")
  private String action;
  @JsonProperty("IsMandatory")
  private Boolean isMandatory;
  @JsonProperty("TestCaseId")
  private Long testCaseId;
  @JsonProperty("KibbutzPluginNlpId")
  private Long addonActionId;
  @JsonProperty("TestComponentId")
  private Long stepGroupId;
  @JsonProperty("CustomFields")
  private String customFields;
  @JsonProperty("DataMap")
  private String dataMap;
  @JsonProperty("ExceptedResult")
  private String exceptedResult;
  @JsonProperty("TemplateId")
  private Integer naturalTextActionId;
  @JsonProperty("Type")
  private TestStepType type;
  @JsonProperty("WaitTime")
  private Integer waitTime;
  @JsonProperty("ConditionType")
  private TestStepConditionType conditionType;
  @JsonProperty("ParenId")
  private Long parentId;
  @JsonProperty("IsManual")
  private Boolean IsManual;
  @JsonIgnore
  private String testStepKey;
  @JsonIgnore
  private Long copiedFrom;
  @JsonProperty("PhoneNumberId")
  private String phoneNumberId;
  @JsonProperty("MailBoxId")
  private Long mailBoxId;
  @JsonProperty("CreatedBy")
  private Long createdBy;
  @JsonProperty("UpdatedBy")
  private Long updatedBy;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  @JsonProperty("CreatedDate")
  private Timestamp createdDate;
  @JsonProperty("UpdatedDate")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp updatedDate;
  @JsonProperty("Disabled")
  private Boolean disabled;
  @JsonProperty("IgnoreStepResult")
  private Boolean ignoreStepResult;
  @JsonIgnore
  private String typeName;


  public TestStepCloudDataMap getDataMap() {
    return new ObjectMapperService().parseJson(dataMap, TestStepCloudDataMap.class);
  }

  public void setDataMap(TestStepCloudDataMap testStepDataMap) {
    this.dataMap = new ObjectMapperService().convertToJson(testStepDataMap);
  }

}
