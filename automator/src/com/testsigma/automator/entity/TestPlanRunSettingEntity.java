package com.testsigma.automator.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class TestPlanRunSettingEntity implements Serializable {
  private Long id;
  private String name;
  private Integer elementTimeOut;
  private Integer pageTimeOut;
  private Long workspaceVersionId;
  private String mailList;
  private Screenshot screenshot;
  private Long environmentParameters;
  private RecoverAction recoveryAction;
  private OnAbortedAction onAbortedAction;
  private PreRequisiteAction onSuitePreRequisiteFail;
  private PreRequisiteAction onTestcasePreRequisiteFail;
  private RecoverAction onStepPreRequisiteFail;
  private Boolean hasSuggestionFeature = false;
  private boolean visualTestingEnabled = false;
  private Boolean retrySessionCreation = false;
  private Integer retrySessionCreationTimeout;
}
