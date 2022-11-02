package com.testsigma.model.recorder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Embeddable;

@Data
@Embeddable
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestStepRecorderForLoop {
    @JsonProperty("loop_start")
    private int startIndex;
    @JsonProperty("loop_end")
    private int endIndex;
    @JsonProperty("test_data_id")
    private Long testDataId;
}
