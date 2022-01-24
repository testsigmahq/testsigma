package com.testsigma.dto;

import lombok.Data;

@Data
public class ServerDTO {
  private Boolean consent;
  private Boolean consentRequestDone;
  private Boolean onboarded;
}
