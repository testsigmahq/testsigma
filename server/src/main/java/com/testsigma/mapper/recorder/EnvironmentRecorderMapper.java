package com.testsigma.mapper.recorder;

import com.testsigma.dto.EnvironmentDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface EnvironmentRecorderMapper {

    @Mapping(target = "updatedById", ignore = true)
    @Mapping(target = "passwords", ignore = true)
    @Mapping(target = "createdById", ignore = true)
    com.testsigma.model.recorder.EnvironmentDTO mapDTO(EnvironmentDTO environmentDTO);
}
