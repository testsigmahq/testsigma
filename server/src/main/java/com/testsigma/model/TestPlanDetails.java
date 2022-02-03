package com.testsigma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestPlanDetails {

  @JsonProperty("page_timeout")
  private Integer pageTimeout;
  @JsonProperty("element_timeout")
  private Integer elementTimeout;
  @JsonProperty("recovery_action")
  private RecoverAction recoveryAction;
  @JsonProperty("on_aborted_action")
  private OnAbortedAction onAbortedAction;
  @JsonProperty("screenshot_option")
  private Screenshot screenshotOption;
  @JsonProperty("group_prerequisite_fail")
  private PreRequisiteAction groupPrerequisiteFail;
  @JsonProperty("test_case_prerequisite_fail")
  private PreRequisiteAction testCasePrerequisiteFail;
  @JsonProperty("test_step_prerequisite_fail")
  private RecoverAction testStepPrerequisiteFail;

  @JsonProperty("environment_param_name")
  private String environmentParamName;
}
