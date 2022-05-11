/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum TestStepType {
  NOT_USED,
  ACTION_TEXT,
  STEP_GROUP,
  REST_STEP,
  FOR_LOOP,
  WHILE_LOOP,
  BREAK_LOOP,
  CONTINUE_LOOP,
  NLP_TEXT,
  CUSTOM_FUNCTION
}
