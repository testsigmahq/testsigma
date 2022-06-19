package com.testsigma.dto.export;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testsigma.model.*;
import com.testsigma.service.ObjectMapperService;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.Map;

@Log4j2
public class TestStepDataMapDeserializer extends JsonDeserializer<TestStepCloudDataMap> {
  @Override
  public TestStepCloudDataMap deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
    TreeNode treeNode = jsonParser.getCodec().readTree(jsonParser);
    ObjectMapperService mapperService = new ObjectMapperService();
    try {
      TestStepCloudDataMap testStepCloudDataMap = mapperService.parseJson(treeNode.toString(), TestStepCloudDataMap.class);
      log.info(testStepCloudDataMap.toString().trim());
      return testStepCloudDataMap;
    } catch (Exception e) {
      Map<String, String> map = mapperService.parseJson(treeNode.toString(), Map.class);
      TestStepCloudDataMap testStepDataMap = new TestStepCloudDataMap();
      log.info("Parsing json to map: " + map);
      if (map.containsKey("test-data")) {
        if (treeNode.get("test-data").get("test-data")!=null){
          Map<String, String> testData = mapperService.parseJson(treeNode.get("test-data").get("test-data").toString(), Map.class);
          testStepDataMap.setTestData(testData.get("value").toString());
          testStepDataMap.setTestDataType(testData.get("type").toString());
          if (testData.containsKey("test-data-function")) {
            testStepDataMap.setTestDataFunction(new ObjectMapper().convertValue(map.get("test-data-function"),
                    DefaultDataGenerator.class));
          }
          if (testData.containsKey("kibbutz_test_data_function")) {
            testStepDataMap.setKibbutzTDF(new ObjectMapper().convertValue(map.get("kibbutz_test_data_function"),
                    AddonTestStepTestData.class));
          }
        }
        else {
          testStepDataMap.setTestData(map.get("test-data").toString());
          testStepDataMap.setTestDataType(map.get("test-data-type").toString());
          if (map.containsKey("test-data-function")) {
            testStepDataMap.setTestDataFunction(new ObjectMapper().convertValue(map.get("test-data-function"),
                    DefaultDataGenerator.class));
          }
          if (map.containsKey("kibbutz_test_data_function")) {
            testStepDataMap.setKibbutzTDF(new ObjectMapper().convertValue(map.get("kibbutz_test_data_function"),
                    AddonTestStepTestData.class));
          }
        }
      }
      if (map.containsKey("condition_if")) {
        testStepDataMap.setIfConditionExpectedResults(map.get("condition_if"));
      }
      if (map.containsKey("condition-type")) {
        testStepDataMap.setIfConditionExpectedResults(map.get("condition-type"));
      }
      if (map.containsKey("custom-step")) {
        testStepDataMap.setCustomStep(new ObjectMapper().convertValue(map.get("custom-step"), TestStepCustomStep.class));
      }
      if (map.containsKey("ui-identifier")) {
        testStepDataMap.setElement(map.get("ui-identifier"));
      }
      if (map.containsKey("from-ui-identifier")) {
        testStepDataMap.setFromElement(map.get("from-ui-identifier"));
      }
      if (map.containsKey("to-ui-identifier")) {
        testStepDataMap.setToElement(map.get("to-ui-identifier"));
      }
      if (map.containsKey("attribute")) {
        testStepDataMap.setAttribute(map.get("attribute"));
      }
      if (map.containsKey("for_loop")) {
        testStepDataMap.setForLoop(new ObjectMapper().convertValue(map.get("for_loop"), TestStepCloudForLoop.class));
      }
      if (map.containsKey("while_loop")) {
        testStepDataMap.setWhileLoop(new ObjectMapper().convertValue(map.get("while_loop"), TestStepWhileLoop.class));
      }
      if (map.containsKey("whileCondition")) {
        testStepDataMap.setWhileCondition(map.get("whileCondition"));
      }
      log.info("Parsed json to testStepDataMap: " + testStepDataMap);
      return testStepDataMap;
    }
  }
}
