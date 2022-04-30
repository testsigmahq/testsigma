/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.dto.export;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.testsigma.annotation.JsonListRootName;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@JsonListRootName(name = "TestMachines")
@JsonRootName(value = "TestMachine")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestDeviceCloudXMLDTO extends BaseXMLDTO {
  @JsonProperty("Id")
  private Long id;
  @JsonProperty("ExecutionId")
  private Long testPlanId;
  @JsonProperty("Title")
  private String title;
  @JsonProperty("TargetMachine")
  private Long targetMachine;
  @JsonProperty("DeviceId")
  private Long deviceId;
  @JsonProperty("PlatformOsVersionId")
  private Long platformOsVersionId;
  @JsonProperty("PlatformBrowserVersionId")
  private Long platformBrowserVersionId;
  @JsonProperty("PlatformScreenResolutionId")
  private Long platformScreenResolutionId;
  @JsonProperty("PlatformDeviceId")
  private Long platformDeviceId;
  @JsonProperty("OnlyMandatory")
  private Boolean onlyMandatory;
  @JsonProperty("Disable")
  private Boolean disable = false;
  @JsonProperty("CreatedBy")
  private Long createdBy;
  @JsonProperty("UpdatedBy")
  private Long updatedBy;
  @JsonProperty("CreatedDate")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp createdDate;
  @JsonProperty("UpdatedDate")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp updatedDate;
  @JsonProperty("MatchBrowserVersion")
  private Boolean matchBrowserVersion = false;
  @JsonProperty("RunInParallel")
  private Boolean runInParallel = Boolean.FALSE;
  @JsonProperty("CreateSessionAtCaseLevel")
  private Boolean createSessionAtCaseLevel = Boolean.FALSE;
  @JsonProperty("IsHeadless")
  private Boolean isHeadless;
  @JsonProperty("EnvParametersName")
  private String envParametersName;
  @JsonProperty("SuiteIds")
  private List<Long> suiteIds;
}
