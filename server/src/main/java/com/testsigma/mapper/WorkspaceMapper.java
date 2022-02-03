/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.mapper;

import com.testsigma.dto.WorkspaceDTO;
import com.testsigma.dto.export.ApplicationXMLDTO;
import com.testsigma.model.Workspace;
import com.testsigma.web.request.WorkspaceRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface WorkspaceMapper {
  List<ApplicationXMLDTO> mapApplications(List<Workspace> workspaces);

  WorkspaceDTO map(Workspace workspace);

  List<WorkspaceDTO> map(List<Workspace> workspaces);

  Workspace map(WorkspaceRequest request);

  void merge(@MappingTarget Workspace workspace, WorkspaceRequest request);
}
