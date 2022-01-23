package com.testsigma.web.request;

import lombok.Data;

@Data
public class RuntimeRequest {
  private String name;
  private String value;
  private String sessionId;
}
