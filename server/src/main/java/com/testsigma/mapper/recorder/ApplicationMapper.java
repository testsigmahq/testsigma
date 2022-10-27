package com.testsigma.mapper.recorder;

import com.testsigma.dto.WorkspaceDTO;
import com.testsigma.model.recorder.ApplicationDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ApplicationMapper {

    @Mapping(target = "updatedById", ignore = true)
    @Mapping(target = "customFields", ignore = true)
    @Mapping(target = "createdById", ignore = true)
    @Mapping(source = "workspaceType", target = "applicationType")
    ApplicationDTO mapDTO(WorkspaceDTO workspaceDTO);
}
