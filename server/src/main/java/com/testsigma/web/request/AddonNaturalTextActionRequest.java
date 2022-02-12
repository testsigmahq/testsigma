/*
 * *****************************************************************************
 *  Copyright (C) 2019 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.web.request;

import com.testsigma.model.AddonApplicationType;
import com.testsigma.model.StepActionType;
import lombok.Data;

import java.util.List;

@Data
public class AddonNaturalTextActionRequest {
  private String fullyQualifiedName;
  private String naturalText;
  private AddonApplicationType workspaceType;
  private String description;
  private Boolean deprecated;
  private List<AddonNaturalTextActionParameterRequest> parameters;
  private StepActionType stepActionType;
}
