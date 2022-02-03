package com.testsigma.dto;

import lombok.Data;

@Data
public class ChromeExtensionDetailsDTO {
  private Long id;
  private Long tenantId;
  private String excludeAttributes;
  private String excludeClasses;
  private String includeClasses;
  private String includeAttributes;
  private String chromeExtensionDetails;
  private String userDefinedAttributes;
}
