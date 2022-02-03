/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.dto;

import lombok.Data;

@Data
public class NaturalTextActionDataDTO {
  public String testData;
  public String element;
  public String attribute;
  public String fromElement;
  public String toElement;
}
