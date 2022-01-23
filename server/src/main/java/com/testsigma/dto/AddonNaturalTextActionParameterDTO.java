package com.testsigma.dto;

import com.testsigma.model.KibbutzActionParameterType;
import lombok.Data;

import java.util.List;

@Data
public class AddonNaturalTextActionParameterDTO {
  private Long id;
  private String name;
  private String reference;
  private String description;
  private KibbutzActionParameterType type;
  private List allowedValues;
}
