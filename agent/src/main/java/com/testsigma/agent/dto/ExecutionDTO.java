/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.dto;

import com.testsigma.automator.entity.TestDeviceEntity;
import lombok.Data;

@Data
public class ExecutionDTO {
  TestDeviceEntity environment;
}
