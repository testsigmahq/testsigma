package com.testsigma.dto;


import com.testsigma.model.KibbutzPluginTestDataFunctionParameterType;
import lombok.Data;

@Data
public class KibbutzPluginTestDataFunctionParameterDTO {
  private Long id;
  private String name;
  private String reference;
  private String description;
  private KibbutzPluginTestDataFunctionParameterType type;
}
