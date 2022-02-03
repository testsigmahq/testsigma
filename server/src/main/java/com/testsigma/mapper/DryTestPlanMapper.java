package com.testsigma.mapper;

import com.testsigma.dto.DryTestPlanDTO;
import com.testsigma.model.DryTestPlan;
import com.testsigma.model.TestDeviceSettings;
import com.testsigma.web.request.DryTestPlanRequest;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface DryTestPlanMapper {

  DryTestPlan map(DryTestPlanRequest dryTestPlanRequest);

  TestDeviceSettings map(com.testsigma.web.request.TestDeviceSettings settings);

  List<DryTestPlanDTO> mapList(List<DryTestPlan> content);
}
