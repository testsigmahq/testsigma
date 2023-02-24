package com.testsigma.mapper.recorder;

import com.testsigma.dto.TestStepDTO;
import com.testsigma.model.ResultConstant;
import com.testsigma.model.TestStepDataMap;
import com.testsigma.model.TestStepNlpData;
import com.testsigma.model.TestStepType;
import com.testsigma.model.recorder.TestStepRecorderDTO;
import com.testsigma.model.recorder.TestStepRecorderDataMap;
import com.testsigma.model.recorder.TestStepRecorderRequest;
import com.testsigma.model.*;
import com.testsigma.model.recorder.*;
import com.testsigma.service.ObjectMapperService;
import com.testsigma.web.request.TestStepRequest;
import org.mapstruct.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface TestStepRecorderMapper {

    @Mapping(target = "kibbutzPluginNlpData", expression = "java(org.apache.commons.collections4.MapUtils.isEmpty(mapKibbutzData(testStepDTO).getTestData()) || org.apache.commons.collections4.MapUtils.isEmpty(mapKibbutzData(testStepDTO).getUiIdentifiers()) ? null : mapKibbutzData(testStepDTO))")
    @Mapping(target = "uiIdentifierDTO", ignore = true, defaultValue = "null")
    @Mapping(target = "testComponentId", source = "stepGroupId")
    @Mapping(target = "templateId", source = "naturalTextActionId")
    @Mapping(target = "stepDescription", ignore = true)
    @Mapping(target = "screenShotURL", ignore = true)
    @Mapping(target = "pageSourceUrl", ignore = true)
    @Mapping(target = "pageSource", ignore = true)
    @Mapping(target = "mailBoxId", ignore = true)
    @Mapping(target = "kibbutzPluginNlpId", source = "addonActionId")
    @Mapping(target = "invalidUiIdentifierList", ignore = true, defaultValue = "null")
    @Mapping(target = "invalidTestDataList", ignore = true, defaultValue = "null")
    @Mapping(target = "importedId", ignore = true)
    @Mapping(target = "hasInvalidUiIdentifier", ignore = true)
    @Mapping(target = "hasInvalidTestData", ignore = true)
    @Mapping(target = "dataMap", expression = "java(testStepDTO.mapTestData())")
    @Mapping(target = "componentTestCaseEntity", ignore = true)
    @Mapping(target = "blockId", ignore = true)
    @Mapping(target = "type", expression = "java(gettType(testStepDTO))")
    TestStepRecorderDTO mapDTO(TestStepDTO testStepDTO);

    @Mapping(target = "uiIdentifiers", source = "testStepDTO.addonElements")
    @Mapping(target = "testData", expression = "java(mapKibbutzTestData(testStepDTO.getAddonTestData()))")
    KibbutzPluginNLPData mapKibbutzData(TestStepDTO testStepDTO);

    Map<String, KibbutzTestStepTestData> mapKibbutzTestData(Map<String, AddonTestStepTestData> addonTestData);

    List<TestStepRecorderDTO> mapDTOs(List<TestStepDTO> testStepDTO);


    @Mapping(target = "ifConditionExpectedResults", expression = "java(mapIfConditionExpectedResults(testStepRecorderRequest))")
    @Mapping(target = "toElement", source = "testStepRecorderRequest.dataMap.fromUiIdentifier")
    @Mapping(target = "dataMap", expression = "java(mapDataMap(testStepRecorderRequest.getDataMap()))")
    @Mapping(target = "stepGroupId", source = "testComponentId")
    @Mapping(target = "naturalTextActionId", source = "templateId")
    @Mapping(target = "addonTestData", source = "kibbutzPluginNlpData.testData")
    @Mapping(target = "addonTDF", ignore = true)
    @Mapping(target = "addonNaturalTextActionData", ignore = true)
    @Mapping(target = "addonElements", source = "kibbutzPluginNlpData.uiIdentifiers")
    @Mapping(target = "addonActionId", source = "kibbutzPluginNlpId")
    TestStepRequest mapRequest(TestStepRecorderRequest testStepRecorderRequest);

    TestStepDataMap mapDataMap(TestStepRecorderDataMap testStepRecorderDataMap);

    default ResultConstant[] mapIfConditionExpectedResults(TestStepRecorderRequest testStepRecorderRequest) {
        Optional<Object> ifCondtionExpectedResults = Optional.ofNullable(testStepRecorderRequest)
                .map(request -> request.getDataMap())
                .map(dataMap -> dataMap.getIfConditionExpectedResults());

        if(ifCondtionExpectedResults.isPresent()) {
            ObjectMapperService mapperService = new ObjectMapperService();
            return mapperService.parseJson(mapperService.convertToJson(ifCondtionExpectedResults.get()), ResultConstant[].class);
        }
        return null;
    }

    default String mapElement(TestStepRecorderRequest testStepRecorderRequest) {
        if(testStepRecorderRequest.getElementRequest() != null) {
            return testStepRecorderRequest.getElementRequest().getName();
        } else if(testStepRecorderRequest.getDataMap() != null) {
            return testStepRecorderRequest.getDataMap().getUiIdentifier();
        }
        return null;
    }*/

    default TestStepType gettType(TestStepDTO dto) {
        TestStepType type = dto.getType();
        if(type == TestStepType.ACTION_TEXT) {
            return TestStepType.NLP_TEXT;
        }
        return type;
    }
}
