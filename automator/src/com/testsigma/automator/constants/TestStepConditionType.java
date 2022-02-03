/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.constants;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum TestStepConditionType {
  CONDITION_IF, CONDITION_IFNOT, CONDITION_ELSE_IF, CONDITION_ELSE_IFNOT, CONDITION_ELSE, LOOP_FOR, LOOP_WHILE
}
