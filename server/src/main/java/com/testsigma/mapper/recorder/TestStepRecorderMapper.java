package com.testsigma.mapper.recorder;

import com.testsigma.dto.TestStepDTO;
import com.testsigma.model.recorder.TestStepRecorderDTO;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface TestStepRecorderMapper {

    @Mapping(target = "uiIdentifierDTO", ignore = true)
    @Mapping(target = "testComponentId", source = "stepGroupId")
    @Mapping(target = "templateId", source = "naturalTextActionId")
    @Mapping(target = "stepDescription", ignore = true)
    @Mapping(target = "screenShotURL", ignore = true)
    @Mapping(target = "pageSourceUrl", ignore = true)
    @Mapping(target = "pageSource", ignore = true)
    @Mapping(target = "mailBoxId", ignore = true)
    @Mapping(target = "kibbutzPluginNlpId", source = "addonActionId")
    @Mapping(target = "invalidUiIdentifierList", ignore = true)
    @Mapping(target = "invalidTestDataList", ignore = true)
    @Mapping(target = "importedId", ignore = true)
    @Mapping(target = "hasInvalidUiIdentifier", ignore = true)
    @Mapping(target = "hasInvalidTestData", ignore = true)
    @Mapping(target = "dataMap", expression = "java(testStepDTO.mapTestData())")
    @Mapping(target = "componentTestCaseEntity", ignore = true)
    @Mapping(target = "blockId", ignore = true)
    TestStepRecorderDTO mapDTO(TestStepDTO testStepDTO);

    List<TestStepRecorderDTO> mapDTOs(List<TestStepDTO> testStepDTO);
}
