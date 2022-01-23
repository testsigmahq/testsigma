package com.testsigma.util;

import java.util.List;
import java.util.Map;

public interface RowAdapter {
  Object[] getRowObjects(Map<String, Integer> nameIndexMap, List<Object> fieldValueArray, Object[] commonValues, int index) throws Exception;

  Object getRowObjects(Map<String, Integer> nameIndexMap, List<Object> fieldValueArray, List<Object> columnNames, Object[] commonValues) throws Exception;
}
