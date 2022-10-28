package com.testsigma.mapper.recorder;

import com.testsigma.dto.WorkspaceDTO;
import com.testsigma.dto.WorkspaceVersionDTO;
import com.testsigma.model.recorder.ApplicationDTO;
import com.testsigma.model.recorder.ApplicationVersionDTO;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ApplicationMapper {

    @Mapping(target = "updatedById", ignore = true)
    @Mapping(target = "customFields", ignore = true)
    @Mapping(target = "createdById", ignore = true)
    @Mapping(source = "workspaceType", target = "applicationType")
    ApplicationDTO mapDTO(WorkspaceDTO workspaceDTO);

    List<ApplicationDTO> mapDTOs(List<WorkspaceDTO> workspaceDTOs);


    @Mapping(target = "updatedById", ignore = true)
    @Mapping(target = "startTime", ignore = true)
    @Mapping(target = "endTime", ignore = true)
    @Mapping(target = "customFields", ignore = true)
    @Mapping(target = "createdById", ignore = true)
    @Mapping(source = "workspaceId", target = "applicationId")
    @Mapping(target = "application", expression = "java(mapDTO(workspaceVersionDTO.getWorkspace()))")
    ApplicationVersionDTO mapApplicationVersionDTO(WorkspaceVersionDTO workspaceVersionDTO);

    List<ApplicationVersionDTO> mapApplicationVersionDTOs(List<WorkspaceVersionDTO> workspaceVersionDTOs);
}
