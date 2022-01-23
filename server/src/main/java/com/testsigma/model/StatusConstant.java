/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StatusConstant {
  STATUS_CREATED,
  STATUS_QUEUED,
  STATUS_PRE_FLIGHT,
  STATUS_IN_PROGRESS,
  STATUS_COMPLETED
}
