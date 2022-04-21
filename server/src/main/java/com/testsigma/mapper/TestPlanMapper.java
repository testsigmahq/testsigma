/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.mapper;

import com.testsigma.dto.TestPlanSettingEntityDTO;
import com.testsigma.dto.TestDeviceSettingsDTO;
import com.testsigma.dto.TestPlanDTO;
import com.testsigma.dto.export.TestPlanXMLDTO;
import com.testsigma.model.AbstractTestPlan;
import com.testsigma.model.TestDevice;
import com.testsigma.model.TestDeviceSettings;
import com.testsigma.model.TestPlan;
import com.testsigma.web.request.TestDeviceRequest;
import com.testsigma.web.request.TestPlanRequest;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface TestPlanMapper {
  TestPlan map(TestPlanRequest testPlanRequest);

  List<TestPlanXMLDTO> mapToXMLDTOList(List<TestPlan> applications);

  @Mapping(target = "environmentId", expression = "java(testPlanRequest.getEnvironmentId())")
  void merge(@MappingTarget TestPlan testPlan, TestPlanRequest testPlanRequest);

  default List<TestDevice> merge(List<TestDeviceRequest> sourceList, List<TestDevice> targetList) {
    List<TestDevice> newList = new ArrayList<>();
    for (TestDeviceRequest environmentRequest : sourceList) {
      TestDevice target = new TestDevice();
      if (environmentRequest.getId() != null) {
        for (TestDevice environment : targetList) {
          if (environmentRequest.getId().equals(environment.getId())) {
            target = environment;
            break;
          }
        }
      }
      merge(target, environmentRequest);
      newList.add(target);
    }
    return newList;
  }


  void merge(@MappingTarget TestDevice environment, TestDeviceRequest request);


  TestDevice map(TestDeviceRequest request);

  TestDeviceSettings map(com.testsigma.web.request.TestDeviceSettings request);

  TestDeviceSettingsDTO mapToDTO(TestDeviceSettings settings);

  TestPlanSettingEntityDTO mapSettings(AbstractTestPlan testPlan);

  List<TestPlanDTO> mapTo(List<TestPlan> testPlans);

  @Mapping(target = "lastRun.testPlan", ignore = true)
  TestPlanDTO mapTo(TestPlan testPlan);
}
