/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.mapper;

import com.testsigma.dto.AddonNaturalTextActionDTO;
import com.testsigma.dto.AddonNaturalTextActionParameterEntityDTO;;
import com.testsigma.dto.KibbutzPluginTestDataFunctionDTO;
import com.testsigma.dto.KibbutzPluginTestDataFunctionParameterEntityDTO;
import com.testsigma.model.*;
import com.testsigma.web.request.AddonNaturalTextActionRequest;
import com.testsigma.web.request.AddonRequest;
import com.testsigma.web.request.AddonNaturalTextActionParameterRequest;
import com.testsigma.web.request.KibbutzPluginTestDataFunctionRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface AddonMapper {
  AddonNaturalTextActionDTO map(AddonNaturalTextAction addonNaturalTextAction);

  KibbutzPluginTestDataFunctionDTO map(KibbutzPluginTestDataFunction addonNaturalTextAction);

  Addon mapRequest(AddonRequest request);

  List<AddonNaturalTextAction> mapAction(List<AddonNaturalTextActionRequest> actionRequests);

  List<AddonNaturalTextActionParameter> mapParams(List<AddonNaturalTextActionParameterRequest> parameterRequests);

  List<AddonNaturalTextActionParameterEntityDTO> mapParamsEntity(List<AddonNaturalTextActionParameter> parameters);

  List<KibbutzPluginTestDataFunctionParameterEntityDTO> mapTDFParamsEntity(List<KibbutzPluginTestDataFunctionParameter> parameters);

  @Mapping(target = "workspaceType", expression = "java(actionRequest.getWorkspaceType().getWorkspaceType())")
  AddonNaturalTextAction mapAction(AddonNaturalTextActionRequest actionRequest);

  KibbutzPluginTestDataFunction mapTestDataFunction(KibbutzPluginTestDataFunctionRequest tdfRequest);

  void merge(Addon plugin, @MappingTarget Addon dbPlugin);

  void merge(AddonNaturalTextAction action, @MappingTarget AddonNaturalTextAction actionDB);

  void merge(KibbutzPluginTestDataFunction tdf,@MappingTarget KibbutzPluginTestDataFunction tdfDB);

  List<AddonNaturalTextActionDTO> mapToDTO(List<AddonNaturalTextAction> actions);

  List<KibbutzPluginTestDataFunctionDTO> mapTDFToDTO(List<KibbutzPluginTestDataFunction> tdf);

}
