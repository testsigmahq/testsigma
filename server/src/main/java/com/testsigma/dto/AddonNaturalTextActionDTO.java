package com.testsigma.dto;

import com.testsigma.model.AddonNaturalTextActionParameter;
import com.testsigma.model.StepActionType;
import com.testsigma.model.WorkspaceType;
import lombok.Data;

import java.util.List;

@Data
public class AddonNaturalTextActionDTO {
  private Long id;
  private String naturalText;
  private String description;
  private WorkspaceType workspaceType;
  private Boolean deprecated;
  private List<AddonNaturalTextActionParameterDTO> parameters;
  private StepActionType stepActionType;
}
