
package com.testsigma.web.request;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class TestCaseLoadRequest {
    private Long id;
    private String testDataSetName;
    private Long testCaseResultId;
    private Long parentTestDataId;
    private String sessionId;
    private Long hostStepId;
    private Long hostTestCaseId;
    private String parentHierarchy;
    private String loopIds;
    private Long stepId;
    private Integer index;
    private String iteration;
    private Long testDataSetId;
    private String leftParamValue;
    private String rightParamValue;
}