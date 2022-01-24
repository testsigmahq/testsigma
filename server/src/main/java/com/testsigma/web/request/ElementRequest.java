/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.web.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.testsigma.dto.ElementScreenNameDTO;
import com.testsigma.model.ElementCreateType;
import com.testsigma.model.ElementMetaDataRequest;
import com.testsigma.model.LocatorType;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Log4j2
public class ElementRequest {
  @NotEmpty
  private String name;
  @NotNull
  private ElementCreateType createdType;
  @NotNull
  private LocatorType locatorType;
  private String locatorValue;
  private String attributes;
  private ElementMetaDataRequest metadata;
  private Boolean isDynamic = Boolean.FALSE;
  @NotNull
  private Long workspaceVersionId;
  private Long screenNameId;
  private ElementScreenNameDTO screenNameObj;
}
