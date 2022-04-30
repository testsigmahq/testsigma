package com.testsigma.mapper;

import com.testsigma.dto.export.RestStepCloudXMLDTO;
import com.testsigma.dto.export.RestStepXMLDTO;
import com.testsigma.dto.export.TestStepCloudXMLDTO;
import com.testsigma.dto.export.TestStepXMLDTO;
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

  default List<RestStepXMLDTO> mapRestSteps(List<RestStep> restSteps) {
    List<RestStepXMLDTO> restStepXMLDTOS = new ArrayList<>();
    for (RestStep step : restSteps) {
      restStepXMLDTOS.add(map(step));
    }
      return restStepXMLDTOS;
  }

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "stepId", ignore = true)
  RestStep mapStep(RestStep restStep);

    TestStep copy(TestStep testStep);

  List<TestStep> mapTestStepsCloudList(List<TestStepCloudXMLDTO> readValue);

  List<TestStep> mapTestStepsList(List<TestStepXMLDTO> readValue);

  List<RestStep> mapRestStepsCloudList(List<RestStepCloudXMLDTO> readValue);

  List<RestStep> mapRestStepsList(List<RestStepXMLDTO> readValue);
}
