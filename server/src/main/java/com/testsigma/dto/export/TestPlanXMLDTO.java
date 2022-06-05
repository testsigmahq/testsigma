/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.dto.export;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.testsigma.annotation.JsonListRootName;
import com.testsigma.model.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Set;

@Data
@JsonListRootName(name = "test-plans")
@JsonRootName(value = "test-plan")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestPlanXMLDTO extends BaseXMLDTO {
  @JsonProperty("id")
  private Long id;
  @JsonProperty("application-version-id")
  private Long workspaceVersionId;
  @JsonProperty("last-run-id")
  private Long lastRunId;
  @JsonProperty("name")
  private String name;
  @JsonProperty("description")
  private String description;
  @JsonProperty("execution-lab-type")
  private TestPlanLabType testPlanLabType;
  @JsonProperty("test-plan-type")
  private TestPlanType testPlanType;
  @JsonProperty("element-timeout")
  private Integer elementTimeOut;
  @JsonProperty("page-timeout")
  private Integer pageTimeOut;
  @JsonProperty("environment-id")
  private Long environmentId;
  @JsonProperty("screenshot")
  private Screenshot screenshot;
  @JsonProperty("recovery-action")
  private RecoverAction recoveryAction;
  @JsonProperty("on-aborted-action")
  private OnAbortedAction onAbortedAction;
  @JsonProperty("re-run-type")
  private ReRunType reRunType;
  @JsonProperty("on-suite-pre-requisite-fail")
  private PreRequisiteAction onSuitePreRequisiteFail;
  @JsonProperty("on-testcase-pre-requisite-fail")
  private PreRequisiteAction onTestcasePreRequisiteFail;
  @JsonProperty("on-step-pre-requisite-fail")
  private RecoverAction onStepPreRequisiteFail;
  @JsonProperty("retry-session-creation-timeout")
  private Integer retrySessionCreationTimeout;
  @JsonProperty("retry-session-creation")
  private boolean retrySessionCreation;
  @JsonProperty("created-by-id")
  private Long createdById;
  @JsonProperty("created-date")
  private Timestamp createdDate;
  @JsonProperty("updated-by-id")
  private Long updatedById;
  @JsonProperty("updated-date")
  private Timestamp updatedDate;
  @JsonProperty("match-browser-version")
  private Boolean matchBrowserVersion = Boolean.FALSE;
  @JsonProperty("re-run-on-failure-action-name")
  private String reRunOnFailureActionName;
}
