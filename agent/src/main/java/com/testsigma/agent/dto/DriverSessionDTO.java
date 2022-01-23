/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.dto;

import lombok.Data;

@Data
public class DriverSessionDTO {
  private String sessionId;
  private String status;
  private String hostname;
}
