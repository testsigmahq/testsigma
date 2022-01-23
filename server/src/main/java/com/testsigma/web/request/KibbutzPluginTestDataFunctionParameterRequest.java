/*
 * *****************************************************************************
 *  Copyright (C) 2019 Testsigma Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.web.request;

import com.testsigma.model.KibbutzActionParameterType;
import com.testsigma.model.KibbutzPluginTestDataFunctionParameterType;
import lombok.Data;

@Data
public class KibbutzPluginTestDataFunctionParameterRequest {
  private String name;
  private String reference;
  private String description;
  private KibbutzPluginTestDataFunctionParameterType type;
}
