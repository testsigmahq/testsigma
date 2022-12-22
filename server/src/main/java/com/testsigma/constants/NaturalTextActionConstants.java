package com.testsigma.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NaturalTextActionConstants {
  public final static String TEST_STEP_DATA_MAP_KEY_TEST_DATA = "test-data";
  public final static String TEST_STEP_DATA_MAP_KEY_TEST_DATA_RECORDER = "testData";
  public final static String TEST_STEP_KEY_UI_IDENTIFIER_RECORDER = "uiIdentifier";
  public final static String TEST_STEP_DATA_MAP_KEY_TEST_DATA_TYPE = "test-data-type";
  public final static String TEST_STEP_DATA_MAP_KEY_ELEMENT = "element";
  public final static String TEST_STEP_DATA_MAP_KEY_ATTRIBUTE = "attribute";
  public final static String TEST_STEP_DATA_MAP_KEY_FROM_ELEMENT = "from-element";
  public final static String TEST_STEP_DATA_MAP_KEY_TO_ELEMENT = "to-element";
  public final static String TEST_DATA_PARAMETER_PREFIX = "@";
  public final static String TEST_DATA_ENVIRONMENT_PARAM_PREFIX = "\\*";
  public final static String REST_DATA_PARAM_START_ESCAPED_PATTERN = "@\\|";
  public final static String REST_DATA_PARAM_END_ESCAPED_PATTERN = "\\|";
  public final static String REST_DATA_PARAM_START_PATTERN = "@|";
  public final static String REST_DATA_PARAM_END_PATTERN = "|";
  public final static String REST_DATA_ENVIRONMENT_PARAM_START_ESCAPED_PATTERN = "\\*\\|";
  public final static String REST_DATA_ENVIRONMENT_PARAM_END_ESCAPED_PATTERN = "\\|";
  public final static String REST_DATA_ENVIRONMENT_PARAM_START_PATTERN = "*|";
  public final static String REST_DATA_ENVIRONMENT_PARAM_END_PATTERN = "|";
  public static final int WHILE_LOOP_MAX_LIMIT = 100;
  private static final List<String> fieldPatternList = new ArrayList<String>();
  public static Map<String, String> ERROR_MSGS = new HashMap<String, String>();
  static List<String> fieldDefNames = new ArrayList<String>();

  static {
    fieldPatternList.add("(\\#\\{([^}]+)\\})");
    fieldPatternList.add("(\\$\\{([^}]+)\\})");
    ERROR_MSGS.put(TEST_STEP_DATA_MAP_KEY_ELEMENT, AutomatorMessages.EMPTY_ELEMENT);
    ERROR_MSGS.put(TEST_STEP_DATA_MAP_KEY_TEST_DATA, AutomatorMessages.EMPTY_TEST_DATA);
    ERROR_MSGS.put(TEST_STEP_DATA_MAP_KEY_ATTRIBUTE, AutomatorMessages.EMPTY_ATTRIBUTE);
    ERROR_MSGS.put(TEST_STEP_DATA_MAP_KEY_FROM_ELEMENT, AutomatorMessages.EMPTY_FROM_ELEMENT);
    ERROR_MSGS.put(TEST_STEP_DATA_MAP_KEY_TO_ELEMENT, AutomatorMessages.EMPTY_TO_ELEMENT);

    fieldDefNames.add("element");
    fieldDefNames.add("from-element");
    fieldDefNames.add("to-element");
  }
}
