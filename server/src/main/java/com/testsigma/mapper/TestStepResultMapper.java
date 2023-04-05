/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.mapper;


import com.testsigma.automator.entity.DefaultDataGeneratorsEntity;
import com.testsigma.dto.*;
import com.testsigma.model.*;
import com.testsigma.web.request.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface TestStepResultMapper {

  ElementMetaDataDTO mapMetaData(ElementMetaData elementMetaData);

  @Mapping(target = "currentElement", expression = "java(elementMetaDataDTO.getCurrentElement().toString())")
  ElementMetaData mapMetaData(ElementMetaDataDTO elementMetaDataDTO);

  Element mapFrom(ElementDTO elementDTO);

  @Mapping(target = "metadata",
    expression = "java(this.mapMetadata(testCaseStepResult.getMetadata()))")
  @Mapping(target = "duration", expression = "java(testCaseStepResult.getEndTime().getTime() - testCaseStepResult" +
    ".getStartTime().getTime())")
  @Mapping(target = "stepId", source = "testCaseStepId")
  @IterableMapping(qualifiedByName = "suggestionRequest")
  TestStepResult map(TestStepResultRequest testCaseStepResult);

  StepDetails map(StepDetailsRequest stepDetails);

  @Mapping(target = "forLoop", expression = "java(this.mapForLoop(metadataRequest.getForLoop()))")
  @Mapping(target = "whileLoop", expression = "java(this.mapWhileLoop(metadataRequest.getWhileLoop()))")
  StepResultMetadata mapMetadata(StepResultMetadataRequest metadataRequest);

  StepResultForLoopMetadata mapForLoop(StepResultForLoopMetadataRequest forLoopMetadataRequest);

  StepResultWhileLoopMetadata mapWhileLoop(StepResultWhileLoopMetadataRequest whileLoopMetadataRequest);

  ForLoopConditionDTO map(ForLoopConditionRequest forLoopConditionRequest);

  @Mapping(target = "metadata", expression = "java(this.mapMetadataDTO(testStepResult.getMetadata()))")
  TestStepResultDTO mapDTO(TestStepResult testStepResult);

  StepResultMetadataDTO mapMetadataDTO(StepResultMetadata metadata);

  List<TestStepResultDTO> mapDTO(List<TestStepResult> testStepResult);
}
