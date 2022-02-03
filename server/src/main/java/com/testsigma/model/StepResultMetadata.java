package com.testsigma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.testsigma.serializer.JSONObjectDeserializer;
import com.testsigma.serializer.JSONObjectSerializer;
import lombok.Data;
import org.json.JSONObject;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StepResultMetadata {

  private Long id;
  private String action;
  private String testDataType;
  private String testDataValue;
  private String attribute;
  private StepDetails stepDetails;
  @JsonSerialize(using = JSONObjectSerializer.class)
  @JsonDeserialize(using = JSONObjectDeserializer.class)
  private JSONObject additionalData;
  private Integer preRequisite;
  private RestRequestMetadata reqEntity;
  private RestResponseMetadata restResult;
  @JsonProperty("for_loop")
  private StepResultForLoopMetadata forLoop;
  @JsonProperty("while_loop")
  private StepResultWhileLoopMetadata whileLoop;

  private String log;
  private Object output;
  private Map<String, String> runtimeData;
  private Map<Object, Object> functionStepResultMetadata;
  private Map<Object, Object> snippetResultMetadata;
  private Map<String, Object> testStep;
}
