/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.mapper;

import com.testsigma.dto.AddonNaturalTextActionDTO;
import com.testsigma.dto.AddonNaturalTextActionParameterEntityDTO;;
import com.testsigma.dto.AddonPluginTestDataFunctionDTO;
import com.testsigma.dto.AddonPluginTestDataFunctionParameterEntityDTO;
import com.testsigma.model.*;
import com.testsigma.web.request.AddonNaturalTextActionRequest;
import com.testsigma.web.request.AddonRequest;
import com.testsigma.web.request.AddonNaturalTextActionParameterRequest;
import com.testsigma.web.request.AddonPluginTestDataFunctionRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface AddonMapper {
  AddonNaturalTextActionDTO map(AddonNaturalTextAction addonNaturalTextAction);

  AddonPluginTestDataFunctionDTO map(AddonPluginTestDataFunction addonNaturalTextAction);

  Addon mapRequest(AddonRequest request);

  List<AddonNaturalTextAction> mapAction(List<AddonNaturalTextActionRequest> actionRequests);

  List<AddonNaturalTextActionParameter> mapParams(List<AddonNaturalTextActionParameterRequest> parameterRequests);

  List<AddonNaturalTextActionParameterEntityDTO> mapParamsEntity(List<AddonNaturalTextActionParameter> parameters);

  List<AddonPluginTestDataFunctionParameterEntityDTO> mapTDFParamsEntity(List<AddonPluginTestDataFunctionParameter> parameters);

  @Mapping(target = "workspaceType", expression = "java(actionRequest.getWorkspaceType().getWorkspaceType())")
  AddonNaturalTextAction mapAction(AddonNaturalTextActionRequest actionRequest);

  AddonPluginTestDataFunction mapTestDataFunction(AddonPluginTestDataFunctionRequest tdfRequest);

  void merge(Addon plugin, @MappingTarget Addon dbPlugin);

  void merge(AddonNaturalTextAction action, @MappingTarget AddonNaturalTextAction actionDB);

  void merge(AddonPluginTestDataFunction tdf, @MappingTarget AddonPluginTestDataFunction tdfDB);

  List<AddonNaturalTextActionDTO> mapToDTO(List<AddonNaturalTextAction> actions);

  List<AddonPluginTestDataFunctionDTO> mapTDFToDTO(List<AddonPluginTestDataFunction> tdf);

}
