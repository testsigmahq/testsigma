package com.testsigma.dto;


import lombok.Data;

import java.util.List;

@Data
public class KibbutzPluginTestDataFunctionDTO {
  private Long id;
  //  private String grammar;
  private String fullyQualifiedName;
  private String displayName;
  private String description;
  private Boolean deprecated;
  private Long addonId;
  private String externalUniqueId;
  private List<KibbutzPluginTestDataFunctionParameterDTO> parameters;
}

