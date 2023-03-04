package com.testsigma.mapper;

import com.testsigma.dto.ForLoopConditionDTO;
import com.testsigma.dto.export.*;
import com.testsigma.model.*;
import com.testsigma.service.ObjectMapperService;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface RestStepMapper {
    RestStepXMLDTO map(RestStep restStep);

    default List<RestStepXMLDTO> mapRestSteps(List<RestStep> restSteps) {
        List<RestStepXMLDTO> restStepXMLDTOS = new ArrayList<>();
        for (RestStep step : restSteps) {
            restStepXMLDTOS.add(map(step));
        }
        return restStepXMLDTOS;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "stepId", ignore = true)
    RestStep mapStep(RestStep restStep);

    TestStep copy(TestStep testStep);

    @Mapping(target = "type", expression = "java(getStepType(stepXMLDTO.getType()))")
    TestStep mapXMLToStep(TestStepCloudXMLDTO stepXMLDTO);

    @Mapping(target = "ifConditionExpectedResults", expression = "java(getIfConditionExpectedResults(map.getIfConditionExpectedResults()))")
    @Mapping(target = "testDataFunctionId", expression = "java(getTestDataFunctionId(map.getTestDataFunction()))")
    @Mapping(target = "testDataFunctionArgs", expression = "java(getTestDataFunctionArgs(map.getTestDataFunction()))")
    @Mapping(target = "dataMap", expression = "java(mapDataMap(map))")
    TestStep mapDataMapToStep(TestStepCloudDataMap map);

    TestStep mapForLoopToStep(TestStepCloudForLoop loop);

    TestStepDataMap mapDataMap(TestStepCloudDataMap dataMap);

    void merge(TestStep step, @MappingTarget TestStep testStep);

    default List<TestStep> mapTestStepsCloudList(List<TestStepCloudXMLDTO> readValue) {
        List<TestStep> list = new ArrayList<>();
        for (TestStepCloudXMLDTO stepXMLDTO : readValue) {
            TestStep step = mapXMLToStep(stepXMLDTO);
            if (stepXMLDTO.getDataMap() != null) {
                TestStep testStep = mapDataMapToStep(stepXMLDTO.getDataMap());
                merge(testStep, step);
                if (stepXMLDTO.getDataMap().getForLoop() != null) {
                    TestStep loopStep = mapForLoopToStep(stepXMLDTO.getDataMap().getForLoop());
                    merge(loopStep, step);
                }
            }
            list.add(step);
        }
        return list;
    }

    @Mapping(target = "forLoopConditions", expression = "java(mapToForLoopCondition(readValue.getForLoopCondition()))")
    TestStep mapTestStep(TestStepXMLDTO readValue);

    default List<ForLoopCondition> mapToForLoopCondition(ForLoopConditionDTO forLoopConditionDTO) {
        if(forLoopConditionDTO == null) {
            return null;
        }
        ForLoopCondition forLoopCondition = new ForLoopCondition();
        forLoopCondition.setTestCaseId(forLoopConditionDTO.getTestCaseId());
        forLoopCondition.setIterationType(forLoopConditionDTO.getIterationType());
        forLoopCondition.setTestStepId(forLoopConditionDTO.getTestStepId());
        forLoopCondition.setLeftDataMap(forLoopConditionDTO.getLeftDataMap());
        forLoopCondition.setLeftFunctionId(forLoopConditionDTO.getLeftFunctionId());
        forLoopCondition.setLeftParamType(forLoopConditionDTO.getLeftParamType());
        forLoopCondition.setLeftParamValue(forLoopConditionDTO.getLeftParamValue());
        forLoopCondition.setRightDataMap(forLoopConditionDTO.getRightDataMap());
        forLoopCondition.setRightFunctionId(forLoopConditionDTO.getRightFunctionId());
        forLoopCondition.setRightParamType(forLoopConditionDTO.getRightParamType());
        forLoopCondition.setRightParamValue(forLoopConditionDTO.getRightParamValue());
        forLoopCondition.setOperator(forLoopConditionDTO.getOperator());
        forLoopCondition.setTestDataProfileId(forLoopConditionDTO.getTestDataProfileId());
        return new ArrayList<>(){{add(forLoopCondition);}};
    }

    List<TestStep> mapTestStepsList(List<TestStepXMLDTO> readValue);

    List<RestStep> mapRestStepsCloudList(List<RestStepCloudXMLDTO> readValue);

    List<RestStep> mapRestStepsList(List<RestStepXMLDTO> readValue);

    default TestStepType getStepType(TestStepType type) {
        if (type == TestStepType.NLP_TEXT)
            return TestStepType.ACTION_TEXT;
        else return type;
    }

    default ResultConstant[] getIfConditionExpectedResults(Object constant) {
        if (constant != null) {
            try {
                return (ResultConstant[]) constant;
            } catch (Exception e) {
                if (constant.toString().equals("0"))
                    return new ResultConstant[]{ResultConstant.SUCCESS};
                else return new ResultConstant[]{ResultConstant.FAILURE};
            }
        }
        return null;
    }

    default Long getTestDataFunctionId(CloudTestDataFunction generator){
        if (generator !=null){
            return generator.getId();
        }
        return null;
    }

    default Map<String, String> getTestDataFunctionArgs(CloudTestDataFunction generator){
        if (generator !=null){
            return generator.getTestDataFunctionArgs();
        }
        return null;
    }

    default String convertDataMap(TestStepCloudDataMap testStepDataMap) {
        return new ObjectMapperService().convertToJson(testStepDataMap);
    }
}
