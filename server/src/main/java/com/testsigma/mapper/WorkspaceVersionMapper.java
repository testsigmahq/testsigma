/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.mapper;

import com.testsigma.dto.WorkspaceVersionDTO;
import com.testsigma.dto.export.ApplicationVersionXMLDTO;
import com.testsigma.model.WorkspaceVersion;
import com.testsigma.web.request.WorkspaceVersionRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface WorkspaceVersionMapper {
  List<ApplicationVersionXMLDTO> mapVersions(List<WorkspaceVersion> workspaceVersions);

  WorkspaceVersion copy(WorkspaceVersion workspaceVersion);

  WorkspaceVersionDTO map(WorkspaceVersion workspaceVersion);

  List<WorkspaceVersionDTO> map(List<WorkspaceVersion> workspaceVersion);

  WorkspaceVersion map(WorkspaceVersionRequest request);

  void merge(@MappingTarget WorkspaceVersion workspaceVersion, WorkspaceVersionRequest request);
}
