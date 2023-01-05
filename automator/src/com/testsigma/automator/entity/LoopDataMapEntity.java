package com.testsigma.automator.entity;

import lombok.Data;

@Data
public class LoopDataMapEntity {
    private DefaultDataGeneratorsEntity testDataFunctionEntity;
    private TestDataPropertiesEntity testDataPropertiesEntity;
    private AddonPluginTestDataFunctionEntity kibbutzPluginTDFEntityList;
}
