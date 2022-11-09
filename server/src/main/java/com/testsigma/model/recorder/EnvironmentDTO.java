package com.testsigma.model.recorder;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.testsigma.serializer.JSONObjectSerializer;
import lombok.Data;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.List;

@Data
public class EnvironmentDTO {
    private Long id;
    private String name;
    private String description;
    private Long createdById;
    private Long updatedById;
    private Timestamp updatedDate;
    private Timestamp createdDate;
    private List<String> passwords;
    @JsonSerialize(using = JSONObjectSerializer.class)
    private JSONObject parameters;
}

