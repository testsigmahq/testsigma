package com.testsigma.dto;


import com.testsigma.model.AddonPluginTestDataFunctionParameterType;
import lombok.Data;

@Data
public class AddonPluginTestDataFunctionParameterDTO {
  private Long id;
  private String name;
  private String reference;
  private String description;
  private AddonPluginTestDataFunctionParameterType type;
}
