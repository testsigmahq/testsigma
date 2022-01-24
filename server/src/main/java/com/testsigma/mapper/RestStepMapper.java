package com.testsigma.mapper;

import com.testsigma.dto.export.RestStepXMLDTO;
import com.testsigma.model.RestStep;
import com.testsigma.model.TestStep;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface RestStepMapper {
  RestStepXMLDTO map(RestStep restStep);

  default List<RestStepXMLDTO> mapRestSteps(List<TestStep> restSteps) {
    List<RestStepXMLDTO> restStepXMLDTOS = new ArrayList<>();
    for (TestStep step : restSteps) {
      if (step.getRestStep() != null)
        restStepXMLDTOS.add(map(step.getRestStep()));
    }
    return restStepXMLDTOS;
  }

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "stepId", ignore = true)
  RestStep mapStep(RestStep restStep);
}
