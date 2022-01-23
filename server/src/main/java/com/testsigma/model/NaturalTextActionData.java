/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NaturalTextActionData {
  @JsonProperty("test-data")
  public String testData;
  @JsonProperty("element")
  public String element;
  @JsonProperty("attribute")
  public String attribute;
  @JsonProperty("from-element")
  public String fromElement;
  @JsonProperty("to-element")
  public String toElement;
}
