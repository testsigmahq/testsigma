package com.testsigma.mapper.recorder;

import com.testsigma.dto.TestDataProfileDTO;
import com.testsigma.model.recorder.TestDataDTO;
import com.testsigma.model.recorder.TestDataSetDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface TestDataMapper {

    @Mapping(target = "updatedById", ignore = true)
    @Mapping(target = "passwords", ignore = true)
    @Mapping(target = "createdById", ignore = true)
    @Mapping(target = "columns", ignore = true)
    //@Mapping(target = "data", expression = "java(testDataProfile.getDataForRecorder())")
    TestDataDTO mapDTO(TestDataProfileDTO testDataProfileDTO);

    @Mapping(target = "testDataProfileId", source = "testDataSetDTO.testDataId")
    TestDataSetDTO mapDTO(com.testsigma.dto.TestDataSetDTO testDataSetDTO);
}
