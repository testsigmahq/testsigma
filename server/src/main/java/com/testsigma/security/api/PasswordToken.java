package com.testsigma.security.api;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PasswordToken {
  private final String email;
}
