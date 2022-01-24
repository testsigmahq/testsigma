package com.testsigma.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Getter
public enum TestDataType {

  //@|Parameter|, $|Runtime| ,*|Environment|, !|Function|, ~|Random|
  NOT_USED(0, "Not Used"),
  raw(1, "raw"),
  parameter(2, "parameter"),
  runtime(3, "runtime"),
  environment(4, "environment"),
  random(5, "random"),
  function(6, "function");

  private final Integer id;
  private final String dispName;

  public static Map<Integer, String> getDispNameMap() {
    Map<Integer, String> toReturn = new HashMap<Integer, String>();
    for (TestDataType type : TestDataType.values()) {
      if (type != NOT_USED) {
        toReturn.put(type.getId(), type.getDispName());
      }
    }
    return toReturn;
  }

  public static TestDataType getType(Integer type) {
    Map<Integer, String> toReturn = new HashMap<Integer, String>();
    for (TestDataType ttype : TestDataType.values()) {
      if (ttype.getId() == type) {
        return ttype;
      }
    }
    return null;
  }

  public static TestDataType getTypeFromName(String name) {
    Map<Integer, String> testDataTypeMap = getDispNameMap();
    for (Map.Entry<Integer, String> entry : testDataTypeMap.entrySet()) {
      if (entry.getValue().equals(name)) {
        return TestDataType.getType(entry.getKey());
      }
    }
    return raw;
  }

  public String getDispName() {
    return dispName;
  }

  public boolean typeEquals(String type) {
    return this.dispName.equals(type);
  }
}
