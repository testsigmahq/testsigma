package com.testsigma.model.recorder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Embeddable;

@Data
@Embeddable
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestStepRecorderForLoop {
    @JsonProperty("loopStart")
    private int startIndex;
    @JsonProperty("loopEnd")
    private int endIndex;
    @JsonProperty("testDataId")
    private Long testDataId;
}
