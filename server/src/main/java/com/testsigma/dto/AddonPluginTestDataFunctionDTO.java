package com.testsigma.dto;


import lombok.Data;

import java.util.List;

@Data
public class AddonPluginTestDataFunctionDTO {
  private Long id;
  //  private String grammar;
  private String fullyQualifiedName;
  private String displayName;
  private String description;
  private Boolean deprecated;
  private Long addonId;
  private String externalUniqueId;
  private List<AddonPluginTestDataFunctionParameterDTO> parameters;
}

