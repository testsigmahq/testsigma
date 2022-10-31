package com.testsigma.mapper.recorder;

import com.testsigma.dto.TestDataProfileDTO;
import com.testsigma.model.recorder.TestCaseDTO;
import com.testsigma.model.recorder.TestDataDTO;
import com.testsigma.model.recorder.TestDataSetDTO;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface TestDataMapper {

    @Mapping(target = "applicationVersionId", source = "workspaceVersionId")
    @Mapping(target = "isTestComponent", source = "isStepGroup")
    @Mapping(target = "testData", expression = "java(mapDTO(testCaseDTO.getTestData()))")
    TestCaseDTO mapTestCaseDTO(com.testsigma.dto.TestCaseDTO testCaseDTO);

    List<TestCaseDTO> mapTestCaseDTOs(List<com.testsigma.dto.TestCaseDTO> testCaseDTO);

    @Mapping(target = "updatedById", ignore = true)
    @Mapping(target = "passwords", ignore = true)
    @Mapping(target = "createdById", ignore = true)
    @Mapping(target = "columns", ignore = true)
    @Mapping(target = "data", expression = "java(getDataForRecorder(testDataProfileDTO.getData()))")
    TestDataDTO mapDTO(TestDataProfileDTO testDataProfileDTO);

    List<TestDataDTO> mapDTOs(List<TestDataProfileDTO> testDataProfileDTOs);

    @Mapping(target = "testDataProfileId", source = "testDataSetDTO.testDataId")
    TestDataSetDTO mapTestDataSetDTO(com.testsigma.dto.TestDataSetDTO testDataSetDTO);

    @Mapping(target = "testDataProfileId", source = "testDataSetDTO.testDataId")
    List<TestDataSetDTO> mapTestDataSetDTOs(List<com.testsigma.dto.TestDataSetDTO> testDataSetDTO);

    @Mapping(target = "testDataProfileId", source = "testDataId")
    List<TestDataSetDTO> getDataForRecorder(List<com.testsigma.dto.TestDataSetDTO> testDataSetDTOs);
}
