package com.testsigma.web.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.testsigma.serializer.JSONObjectDeserializer;
import com.testsigma.serializer.JSONObjectSerializer;
import lombok.Data;
import org.json.JSONObject;

import java.util.Map;

@Data
public class StepResultMetadataRequest {

  private Long id;
  private String action;
  private Boolean hasPassword;
  private String testDataType;
  private String testDataValue;
  private String attribute;
  private StepDetailsRequest stepDetails;
  @JsonSerialize(using = JSONObjectSerializer.class)
  @JsonDeserialize(using = JSONObjectDeserializer.class)
  private JSONObject additionalData;
  private Integer preRequisite;
  private RestRequestMetadataRequest reqEntity;
  private RestResponseMetadataRequest restResult;
  @JsonProperty("for_loop")
  private StepResultForLoopMetadataRequest forLoop;
  @JsonProperty("while_loop")
  private StepResultWhileLoopMetadataRequest whileLoop;

  private String log;
  private Object output;
  private Map<String, String> runtimeData;
  private Map<Object, Object> functionStepResultMetadata;
  private Map<Object, Object> snippetResultMetadata;
  private Map<String, Object> testStep;
}
