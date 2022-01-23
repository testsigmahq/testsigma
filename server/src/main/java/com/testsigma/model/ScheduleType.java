/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ScheduleType {
  ONCE, DAILY, WEEKLY, MONTHLY, YEARLY, BIWEEKLY, HOURLY
}
