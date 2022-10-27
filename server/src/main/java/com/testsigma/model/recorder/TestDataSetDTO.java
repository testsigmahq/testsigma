package com.testsigma.model.recorder;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.testsigma.serializer.JSONObjectSerializer;
import lombok.Data;
import org.json.JSONObject;

@Data
public class TestDataSetDTO {
    private String name;
    private Long testDataProfileId;
    private String description;
    private Boolean expectedToFail;
    @JsonSerialize(using = JSONObjectSerializer.class)
    private JSONObject data;
}
