package com.testsigma.web.request;

import lombok.Data;

@Data
public class ChromeExtensionDetailsRequest {
  private Long id;
  private String excludeAttributes;
  private String excludeClasses;
  private String includeClasses;
  private String includeAttributes;
  private String chromeExtensionDetails;
  private String userDefinedAttributes;
}
