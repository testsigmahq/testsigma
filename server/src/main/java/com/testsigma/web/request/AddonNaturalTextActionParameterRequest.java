/*
 * *****************************************************************************
 *  Copyright (C) 2019 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.web.request;

import com.testsigma.model.AddonActionParameterType;
import lombok.Data;

import java.util.List;

@Data
public class AddonNaturalTextActionParameterRequest {
  private String name;
  private String reference;
  private String description;
  private AddonActionParameterType type;
  private List allowedValues;
}
