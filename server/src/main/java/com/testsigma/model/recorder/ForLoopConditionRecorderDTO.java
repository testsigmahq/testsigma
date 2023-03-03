package com.testsigma.model.recorder;

import com.testsigma.model.IterationType;
import com.testsigma.model.Operator;
import lombok.Data;

@Data
public class ForLoopConditionRecorderDTO {
    private ForLoopConditionTestDataDTO testDataProfile;

    private IterationType iterationType;

    private Operator operator;

    private LoopDataMapRecorderDTO leftData;

    private LoopDataMapRecorderDTO rightData;
}
