package com.testsigma.mapper.recorder;

import com.testsigma.dto.TestStepDTO;
import com.testsigma.model.ResultConstant;
import com.testsigma.model.TestStepDataMap;
import com.testsigma.model.TestStepType;
import com.testsigma.model.recorder.TestStepRecorderDTO;
import com.testsigma.model.recorder.TestStepRecorderDataMap;
import com.testsigma.model.recorder.TestStepRecorderRequest;
import com.testsigma.service.ObjectMapperService;
import com.testsigma.web.request.TestStepRequest;
import org.mapstruct.*;

import java.util.List;
import java.util.Optional;

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
    @Mapping(target = "type", expression = "java(gettType(testStepDTO))")
    TestStepRecorderDTO mapDTO(TestStepDTO testStepDTO);

    List<TestStepRecorderDTO> mapDTOs(List<TestStepDTO> testStepDTO);


    @Mapping(target = "ifConditionExpectedResults", expression = "java(mapIfConditionExpectedResults(testStepRecorderRequest))")
    @Mapping(target = "toElement", source = "testStepRecorderRequest.dataMap.fromUiIdentifier")
    @Mapping(target = "dataMap", expression = "java(mapDataMap(testStepRecorderRequest.getDataMap()))")
    @Mapping(target = "stepGroupId", source = "testComponentId")
    @Mapping(target = "naturalTextActionId", source = "templateId")
    @Mapping(target = "fromElement", source = "testStepRecorderRequest.dataMap.toUiIdentifier")
    @Mapping(target = "forLoopTestDataId", source = "testStepRecorderRequest.dataMap.forLoop.testDataId")
    @Mapping(target = "forLoopStartIndex", source = "testStepRecorderRequest.dataMap.forLoop.startIndex")
    @Mapping(target = "forLoopEndIndex", source = "testStepRecorderRequest.dataMap.forLoop.endIndex")
    @Mapping(target = "element", expression = "java(mapElement(testStepRecorderRequest))")
    @Mapping(target = "attribute", source = "testStepRecorderRequest.dataMap.attribute")
    @Mapping(target = "addonTestData", source = "kibbutzPluginNlpData.testData")
    @Mapping(target = "addonTDF", ignore = true)
    @Mapping(target = "addonNaturalTextActionData", ignore = true)
    @Mapping(target = "addonElements", source = "kibbutzPluginNlpData.uiIdentifiers")
    @Mapping(target = "addonActionId", source = "kibbutzPluginNlpId")
    TestStepRequest mapRequest(TestStepRecorderRequest testStepRecorderRequest);

    TestStepDataMap mapDataMap(TestStepRecorderDataMap testStepRecorderDataMap);

    /*default Map<String, TestStepNlpData> mapTestDataValue(TestStepRecorderRequest testStepRecorderRequest) {
        Map<String, TestStepNlpData> result = new HashMap<>();
        TestStepNlpData testStepNlpData = new TestStepNlpData();
        Optional<String> data = Optional.ofNullable(testStepRecorderRequest)
                .map(request -> request.getDataMap())
                .map(dataMap -> dataMap.getTestData())
                .map(testData -> testData.values())
                .map(values -> values.stream().findFirst())
                .map(nlpData -> nlpData.get().getValue());

        Optional<String> type = Optional.ofNullable(testStepRecorderRequest)
                .map(request -> request.getDataMap())
                .map(dataMap -> dataMap.getTestData())
                .map(testData -> testData.values())
                .map(values -> values.stream().findFirst())
                .map(nlpData -> nlpData.get().getType());
        testStepNlpData.setValue(data.isPresent() ? data.get() : null);
        testStepNlpData.setType(type.isPresent() ? type.get() : null);
        result.put("test-data", testStepNlpData);
        return result;
    }

    default Long mapTestDataFunctionId(TestStepRecorderRequest testStepRecorderRequest) {
        Optional<Long> data = Optional.ofNullable(testStepRecorderRequest)
                .map(request -> request.getDataMap())
                .map(dataMap -> dataMap.getTestData())
                .map(testData -> testData.values())
                .map(values -> values.stream().findFirst())
                .map(nlpData -> nlpData.get().getTestDataFunction())
                .map(testDataRecorderFunction -> testDataRecorderFunction.getId());
        return data.isPresent() ? data.get() : null;
    }

    default Map<String, String> mapTestDataFunctionArgs(TestStepRecorderRequest testStepRecorderRequest) {
        Optional<Map<String, String>> data = Optional.ofNullable(testStepRecorderRequest)
                .map(request -> request.getDataMap())
                .map(dataMap -> dataMap.getTestData())
                .map(testData -> testData.values())
                .map(values -> values.stream().findFirst())
                .map(nlpData -> nlpData.get().getTestDataFunction())
                .map(testDataFunction -> testDataFunction.getTestDataFunctionArgs());
        return data.isPresent() ? data.get() : null;
    }

    default String mapTestDataType(TestStepRecorderRequest testStepRecorderRequest) {
        Optional<String> dataType = Optional.ofNullable(testStepRecorderRequest)
                .map(request -> request.getDataMap())
                .map(dataMap -> dataMap.getTestData())
                .map(testData -> testData.values())
                .map(values -> values.stream().findFirst())
                .map(nlpData -> nlpData.get().getType());
        return dataType.isPresent() ? dataType.get() : null;
    }*/

    default ResultConstant[] mapIfConditionExpectedResults(TestStepRecorderRequest testStepRecorderRequest) {
        Optional<Object> ifCondtionExpectedResults = Optional.ofNullable(testStepRecorderRequest)
                .map(request -> request.getDataMap())
                .map(dataMap -> dataMap.getIfConditionExpectedResults());

        if(ifCondtionExpectedResults.isPresent()) {
            ObjectMapperService mapperService = new ObjectMapperService();
            return mapperService.parseJson(ifCondtionExpectedResults.get().toString(), ResultConstant[].class);
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
    }

    default TestStepType gettType(TestStepDTO dto) {
        TestStepType type = dto.getType();
        if(type == TestStepType.ACTION_TEXT) {
            return TestStepType.NLP_TEXT;
        }
        return type;
    }
}
