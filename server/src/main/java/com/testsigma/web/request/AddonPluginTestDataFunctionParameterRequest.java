/*
 * *****************************************************************************
 *  Copyright (C) 2019 Testsigma Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.web.request;

import com.testsigma.model.AddonPluginTestDataFunctionParameterType;
import lombok.Data;

@Data
public class AddonPluginTestDataFunctionParameterRequest {
  private String name;
  private String reference;
  private String description;
  private AddonPluginTestDataFunctionParameterType type;
}
