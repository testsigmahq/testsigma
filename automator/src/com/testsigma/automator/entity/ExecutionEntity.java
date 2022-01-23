package com.testsigma.automator.entity;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
public class ExecutionEntity implements Serializable {

  private Long id;
  private Long exeRunId;
  private String key;
  private String name;
  private String description;
  private Integer elementTimeOut;
  private Integer pageTimeOut;
  private Long workspaceVersionId;
  private String mailList;
  private Screenshot screenShot;
  private RecoverAction recoveryAction;
  private OnAbortedAction onAbortedAction;
  private PreRequisiteAction onGroupPrequisiteFail;
  private PreRequisiteAction onTestcasePrerequisiteFail;
  private RecoverAction onStepPrequisiteFail;
  private String slackUserName;
  private String slackChannel;
  private Boolean slackEnabled;
  private String notificationStatusList;
  private String emailSubject;
  private Timestamp startTime;
  private Timestamp endTime;
  private Integer executionKind;
  private List<TestDeviceEntity> environmentlist = new ArrayList<TestDeviceEntity>();
  private Boolean msTeamsNotificationEnabled;
  private Boolean msTeamsConnectorNotificationEnabled;

  public boolean isSlackNotificationEnabled() {
    return this.slackEnabled;
  }

  public boolean isEmailNotificationEnabled() {
    return StringUtils.isNotBlank(getMailList());
  }
}
