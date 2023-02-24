package com.testsigma.dto;

import com.testsigma.automator.entity.DefaultDataGeneratorsEntity;
import com.testsigma.model.AddonTestStepTestData;
import com.testsigma.model.DefaultDataGenerator;
import lombok.Data;

@Data
public class LoopDataMapDTO {
    public DefaultDataGenerator testDataMap;
    public AddonTestStepTestData kibbutzPluginTDFEntityList;
    public DefaultDataGeneratorsEntity testDataFunctionEntity;
}
