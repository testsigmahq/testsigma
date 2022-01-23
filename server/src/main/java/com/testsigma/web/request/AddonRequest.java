/*
 * *****************************************************************************
 *  Copyright (C) 2019 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.web.request;

import com.testsigma.model.AddonStatus;
import lombok.Data;

import java.util.List;

@Data
public class AddonRequest {
  private String name;
  private String version;
  private String description;
  private String externalUniqueId;
  private String externalInstalledVersionUniqueId;
  private AddonStatus status;
  private List<AddonNaturalTextActionRequest> actions;
  private List<KibbutzPluginTestDataFunctionRequest> testDataFunctions;
}
