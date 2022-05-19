/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.mapper;

import com.testsigma.dto.PrivateGridNodeDTO;
import com.testsigma.model.PrivateGridNode;
import com.testsigma.web.request.PrivateGridNodeRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface PrivateGridNodeMapper {

  @Mapping(target = "browserList", expression = "java(nodeRequest.getNodeBrowserList())")
  PrivateGridNode map(PrivateGridNodeRequest nodeRequest);

  @Mapping(target = "browserList", expression = "java(nodeRequest.getNodeBrowserList())")
  void map(PrivateGridNodeRequest nodeRequest, @MappingTarget PrivateGridNode node);

  @Mapping(target = "browserList", expression = "java(node.getBrowserListDTO())")
  PrivateGridNodeDTO map(PrivateGridNode node);

  List<PrivateGridNodeDTO> mapList(List<PrivateGridNode> node);

}
