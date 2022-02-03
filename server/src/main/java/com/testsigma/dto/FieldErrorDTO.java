/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.dto;

import lombok.Data;

@Data
public class FieldErrorDTO {
  private String field;
  private Object rejectedValue;
  private String message;
  private Boolean bindingFailure;
  private String[] codes;
}
