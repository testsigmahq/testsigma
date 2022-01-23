/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.agent.dto;

import lombok.Data;

@Data
public class ObjectErrorDTO {
  private String objectName;
  private Object source;
  private String defaultMessage;
  private String[] codes;
}
