package com.testsigma.mapper;

import com.testsigma.automator.entity.ForLoopConditionsEntity;
import com.testsigma.dto.DefaultDataGeneratorsDTO;
import com.testsigma.dto.ForLoopConditionDTO;
import com.testsigma.dto.LoopDataMapDTO;
import com.testsigma.dto.export.ForLoopConditionXMLDTO;
import com.testsigma.model.*;
import com.testsigma.model.recorder.*;
import com.testsigma.service.ForLoopConditionService;
import com.testsigma.web.request.ForLoopConditionRequest;
import com.testsigma.web.request.TestCaseFilterRequest;
import org.mapstruct.*;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ForLoopConditionsMapper {

    void merge(@MappingTarget ForLoopCondition forLoopCondition, ForLoopConditionRequest request);

    List<ForLoopConditionDTO> map(List<ForLoopCondition> models);

    List<ForLoopConditionXMLDTO> mapDtos(List<ForLoopCondition> models);

    List<ForLoopCondition> mapEntities(List<ForLoopConditionXMLDTO> dtos);

    ForLoopConditionDTO map(ForLoopCondition model);

    ForLoopCondition copy(ForLoopCondition model);

    ForLoopCondition map(ForLoopConditionRequest request);

    ForLoopCondition map(ForLoopConditionRecorderRequest request);

    @Mapping(target = "argumentTypes", ignore = true)
    DefaultDataGeneratorsDTO map(DefaultDataGenerator testDataFunction);

    @Mapping(target = "isKibbutzFn", source = "isAddonFn")
    KibbutzTestStepTestData map(AddonTestStepTestData addonTestStepTestData);

    ForLoopConditionRecorderDTO mapToRecorderDTO(ForLoopCondition forLoopCondition);

    ForLoopConditionRequest mapRequest(ForLoopConditionRecorderRequest forLoopConditionRecorderRequest);

    default ForLoopConditionRequest getForConditionEntity(ForLoopConditionRecorderRequest forLoopConditionRequest) {
        ForLoopConditionRequest forLoopConditionRequest1 = new ForLoopConditionRequest();

        forLoopConditionRequest1.setTestCaseId(forLoopConditionRequest.getTestCaseId());
        forLoopConditionRequest1.setTestStepId(forLoopConditionRequest.getTestStepId());
        forLoopConditionRequest1.setId(forLoopConditionRequest.getId());
        forLoopConditionRequest1.setIterationType(forLoopConditionRequest.getIterationType());
        forLoopConditionRequest1.setOperator(forLoopConditionRequest.getOperator());
        if(forLoopConditionRequest.getTestDataProfile()!=null)
            forLoopConditionRequest1.setTestDataProfileId(forLoopConditionRequest.getTestDataProfile().getId());

        if (forLoopConditionRequest.getLeftData() != null && forLoopConditionRequest.getLeftData().getType() == TestDataType.raw) {
            forLoopConditionRequest1.setLeftParamValue(forLoopConditionRequest.getLeftData().getValue());
            forLoopConditionRequest1.setLeftParamType(forLoopConditionRequest.getLeftData().getType());
        } else if (forLoopConditionRequest.getLeftData() != null) {
            forLoopConditionRequest1.setLeftParamType(forLoopConditionRequest.getLeftData().getType());
            LoopDataMapDTO leftLoopMapDTO = new LoopDataMapDTO();
            forLoopConditionRequest1.setLeftParamValue(forLoopConditionRequest.getLeftData().getValue());
            leftLoopMapDTO.setTestDataMap(forLoopConditionRequest.getLeftData().getTestDataFunction());
            leftLoopMapDTO.setKibbutzPluginTDFEntityList(forLoopConditionRequest.getLeftData().getKibbutzPluginTDFList());

            Boolean isLeftDataFunctionType = forLoopConditionRequest.getLeftData() != null &&
                    List.of(TestDataType.function)
                            .contains(forLoopConditionRequest1.getLeftParamType());
            if (isLeftDataFunctionType)
                forLoopConditionRequest1.setLeftDataMap(leftLoopMapDTO);

        }

        if (forLoopConditionRequest.getRightData() != null && forLoopConditionRequest.getRightData().getType() == TestDataType.raw) {
            forLoopConditionRequest1.setRightParamValue(forLoopConditionRequest.getRightData().getValue());
            forLoopConditionRequest1.setRightParamType(forLoopConditionRequest.getRightData().getType());
        } else if (forLoopConditionRequest.getRightData() != null) {
            com.testsigma.dto.LoopDataMapDTO rightLoopMapDTO = new com.testsigma.dto.LoopDataMapDTO();
            forLoopConditionRequest1.setRightParamType(forLoopConditionRequest.getRightData().getType());
            forLoopConditionRequest1.setRightParamValue(forLoopConditionRequest.getRightData().getValue());
            rightLoopMapDTO.setTestDataMap(forLoopConditionRequest.getRightData().getTestDataFunction());
            rightLoopMapDTO.setKibbutzPluginTDFEntityList(forLoopConditionRequest.getRightData().getKibbutzPluginTDFList());

            Boolean isRightDataFunctionType = forLoopConditionRequest.getRightData() != null && List.of(TestDataType.function)
                    .contains(forLoopConditionRequest1.getRightParamType());
            if (isRightDataFunctionType)
                forLoopConditionRequest1.setRightDataMap(rightLoopMapDTO);
        }
        return forLoopConditionRequest1;
    }



    default ForLoopConditionRecorderDTO getForLoopConditionDTO(ForLoopCondition forLoopCondition) {
        ForLoopConditionRecorderDTO forLoopConditionDTO = null;
        if (forLoopCondition != null) {
            forLoopConditionDTO = new ForLoopConditionRecorderDTO();
            forLoopConditionDTO.setIterationType(forLoopCondition.getIterationType());
            forLoopConditionDTO.setOperator(forLoopCondition.getOperator());
            if (forLoopCondition.getTestDataProfileId() != null) {
                ForLoopConditionTestDataDTO tdp = new ForLoopConditionTestDataDTO();
                tdp.setId(forLoopCondition.getTestDataProfileId());
                forLoopConditionDTO.setTestDataProfile(tdp);
            }
            LoopDataMapRecorderDTO leftLoopDataMapDTO = null;

            TestDataType leftTestDataType = forLoopCondition.getLeftParamType();
            if (leftTestDataType == TestDataType.raw) {
                leftLoopDataMapDTO = new LoopDataMapRecorderDTO();
                leftLoopDataMapDTO.setType(forLoopCondition.getLeftParamType());
                leftLoopDataMapDTO.setValue(forLoopCondition.getLeftParamValue());
            } else if (forLoopCondition.getLeftParamValue() != null || forLoopCondition.getLeftDataMap() != null) {
                leftLoopDataMapDTO = new LoopDataMapRecorderDTO();
                leftLoopDataMapDTO.setType(forLoopCondition.getLeftParamType());
                if (forLoopCondition.getLeftDataMap() != null) {
                    leftLoopDataMapDTO.setValue(forLoopCondition.getLeftParamValue());
                    leftLoopDataMapDTO.setType(forLoopCondition.getLeftParamType());
                    leftLoopDataMapDTO.setTestDataFunction(forLoopCondition.getLeftDataMap().getTestDataMap());
                    leftLoopDataMapDTO.setKibbutzPluginTDFList(forLoopCondition.getLeftDataMap().getKibbutzPluginTDFEntityList());
                }
            }
            forLoopConditionDTO.setLeftData(leftLoopDataMapDTO);

            LoopDataMapRecorderDTO rightLoopDataMapDTO = null;

            TestDataType rightTestDataType = forLoopCondition.getRightParamType();
            if (rightTestDataType == TestDataType.raw) {
                rightLoopDataMapDTO = new LoopDataMapRecorderDTO();
                rightLoopDataMapDTO.setType(forLoopCondition.getRightParamType());
                rightLoopDataMapDTO.setValue(forLoopCondition.getRightParamValue());

            } else if (forLoopCondition.getRightParamValue() != null) {
                rightLoopDataMapDTO = new LoopDataMapRecorderDTO();
                rightLoopDataMapDTO.setValue(forLoopCondition.getRightParamValue());
                rightLoopDataMapDTO.setType(forLoopCondition.getRightParamType());
                if (forLoopCondition.getRightDataMap() != null) {
                    rightLoopDataMapDTO.setValue(forLoopCondition.getRightParamValue());
                    rightLoopDataMapDTO.setKibbutzPluginTDFList(forLoopCondition.getRightDataMap().getKibbutzPluginTDFEntityList());
                    rightLoopDataMapDTO.setTestDataFunction(forLoopCondition.getRightDataMap().getTestDataMap());
                }
            }

            forLoopConditionDTO.setRightData(rightLoopDataMapDTO);
        }
        return forLoopConditionDTO;
    }
}