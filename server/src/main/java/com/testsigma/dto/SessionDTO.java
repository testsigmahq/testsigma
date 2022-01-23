/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.dto;

import lombok.Data;

@Data
public class SessionDTO {
  String id;
  AuthUserDTO user;
  private String serverUrl;
}
