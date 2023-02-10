package com.testsigma.dto;

import com.testsigma.model.AddonActionParameterType;
import lombok.Data;

import java.util.List;

@Data
public class AddonNaturalTextActionParameterDTO {
  private Long id;
  private String name;
  private String reference;
  private String description;
  private AddonActionParameterType type;
  private String[] allowedValues;
}
