/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.dto.export;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.testsigma.annotation.JsonListRootName;
import com.testsigma.model.*;
import com.testsigma.serializer.JSONArraySerializer;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;

import java.sql.Timestamp;
import java.util.Set;

@Data
@JsonListRootName(name = "TestPlans")
@JsonRootName(value = "TestPlan")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestPlanCloudXMLDTO extends BaseXMLDTO {
  @JsonProperty("Id")
  private Long id;
  @JsonProperty("ApplicationVersionId")
  private Long workspaceVersionId;
  @JsonProperty("LastRunId")
  private Long lastRunId;
  @JsonProperty("Name")
  private String name;
  @JsonProperty("Description")
  private String description;
  @JsonProperty("ExecutionLabType")
  private TestPlanLabType testPlanLabType;
  @JsonProperty("ExecutionType")
  private TestPlanType testPlanType;
  @JsonProperty("MailList")
  private String mailList;
  @JsonProperty("ElementTimeout")
  private Integer elementTimeOut;
  @JsonProperty("PageTimeout")
  private Integer pageTimeOut;
  @JsonProperty("EnvironmentId")
  private Long environmentId;
  @JsonProperty("Screenshot")
  private Screenshot screenshot;
  @JsonProperty("RecoveryAction")
  private RecoverAction recoveryAction;
  @JsonProperty("OnAbortedAction")
  private OnAbortedAction onAbortedAction;
  @JsonProperty("ReRunType")
  private ReRunType reRunType;
  @JsonProperty("OnSuitePreRequisiteFail")
  private PreRequisiteAction onSuitePreRequisiteFail;
  @JsonProperty("OnTestcasePreRequisiteFail")
  private PreRequisiteAction onTestcasePreRequisiteFail;
  @JsonProperty("OnStepPreRequisiteFail")
  private RecoverAction onStepPreRequisiteFail;
  @JsonProperty("IsManual")
  private Boolean isManual;
  @JsonProperty("MsTeamsNotificationEnabled")
  private boolean msTeamsNotificationEnabled;
  @JsonProperty("MsTeamsConnectorNotificationEnabled")
  private boolean msTeamsConnectorNotificationEnabled;
  @JsonProperty("GoogleChatConnectorNotificationEnabled")
  private boolean googleChatConnectorNotificationEnabled;
  @JsonSerialize(using = JSONArraySerializer.class)
  @JsonProperty("NotificationStatusList")
  private String notificationStatusList;
  @JsonProperty("EmailSubject")
  private String emailSubject;
  @JsonProperty("RetrySessionCreationTimeout")
  private Integer retrySessionCreationTimeout;
  @JsonProperty("RetrySessionCreation")
  private boolean retrySessionCreation;
  @JsonProperty("CreatedById")
  private Long createdById;
  @JsonProperty("CreatedDate")
  private Timestamp createdDate;
  @JsonProperty("UpdatedById")
  private Long updatedById;
  @JsonProperty("UpdatedDate")
  private Timestamp updatedDate;
  @JsonProperty("MatchBrowserVersion")
  private Boolean matchBrowserVersion = Boolean.FALSE;
  @JsonProperty("ReRunOnFailureActionName")
  private String reRunOnFailureActionName;
  @JsonProperty("OrphanExecutionEnvironmentIds")
  private Set<Long> orphanExecutionEnvironmentIds;

  public JSONArray getNotificationStatusList() {
    try {
      if ((notificationStatusList == null) || StringUtils.isBlank(notificationStatusList)) {
        return new JSONArray();
      }
      return new JSONArray(notificationStatusList);
    } catch (Exception ex) {
      return null;
    }
  }

  public void setNotificationStatusList(JSONArray notificationStatusList) {
    this.notificationStatusList = notificationStatusList == null ? null : notificationStatusList.toString();
  }

}
