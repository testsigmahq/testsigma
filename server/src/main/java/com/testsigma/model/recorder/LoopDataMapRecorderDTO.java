package com.testsigma.model.recorder;

import com.testsigma.dto.DefaultDataGeneratorsDTO;
import com.testsigma.model.DefaultDataGenerator;
import com.testsigma.model.TestDataType;
import com.testsigma.model.TestStepDataMap;
import lombok.Data;

@Data
public class LoopDataMapRecorderDTO {
    public TestStepDataMap testData;
    public KibbutzTestStepTestData kibbutzPluginTDFList;
    public DefaultDataGenerator testDataFunction;
    private TestDataType type;
    private String value;
}
