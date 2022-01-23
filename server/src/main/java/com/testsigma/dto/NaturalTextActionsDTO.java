/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.dto;

import com.testsigma.model.StepActionType;
import com.testsigma.model.WorkspaceType;
import lombok.Data;

import java.util.List;

@Data
public class NaturalTextActionsDTO {
  private Long id;
  private WorkspaceType workspaceType;
  private String naturalText;
  private NaturalTextActionDataDTO data;
  private String displayName;
  private String action;
  private List allowedValues;
  private StepActionType stepActionType;
}
