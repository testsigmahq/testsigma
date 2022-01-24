/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.dto;

import com.testsigma.model.OnAbortedAction;
import com.testsigma.model.PreRequisiteAction;
import com.testsigma.model.RecoverAction;
import com.testsigma.model.Screenshot;
import lombok.Data;

@Data
public class TestPlanSettingEntityDTO {
  private Integer elementTimeOut;
  private Integer pageTimeOut;
  private Screenshot screenshot;
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
