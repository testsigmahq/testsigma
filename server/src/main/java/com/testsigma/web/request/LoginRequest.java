/******************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 *****************************************************************************/
package com.testsigma.web.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class LoginRequest {
  @NotBlank
  @Email
  String username;
  @NotBlank
  String password;
}
