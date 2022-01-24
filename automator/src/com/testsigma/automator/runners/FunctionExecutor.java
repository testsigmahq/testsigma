package com.testsigma.automator.runners;

import com.testsigma.automator.constants.ErrorCodes;
import com.testsigma.automator.constants.AutomatorMessages;
import com.testsigma.automator.exceptions.TestsigmaInvalidParameterDataException;
import com.testsigma.automator.service.ObjectMapperService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FunctionExecutor {

  protected final ObjectMapperService objectMapperService;

  public FunctionExecutor() {
    this.objectMapperService = new ObjectMapperService();
  }

  private static Class<?> getClassFromName(final String className) {
    switch (className) {
      case "boolean":
        return boolean.class;
      case "byte":
        return byte.class;
      case "short":
        return short.class;
      case "int":
        return int.class;
      case "long":
        return long.class;
      case "float":
        return float.class;
      case "double":
        return double.class;
      case "char":
        return char.class;
      case "void":
        return void.class;
      default:
        try {
          return Class.forName(className);
        } catch (ClassNotFoundException ex) {
          throw new IllegalArgumentException("Class not found: " + className);
        }
    }
  }

  protected List<Class<?>> getArgumentClasses(Map<String, String> argumentTypesMap) throws ClassNotFoundException {
    List<Class<?>> argumentTypes = new ArrayList<>();
    if (argumentTypesMap != null) {
      for (int i = 0; i < argumentTypesMap.size(); i++) {
        argumentTypes.add(getClassFromName(argumentTypesMap.get("arg" + i)));
      }
    }
    return argumentTypes;
  }

  protected List<Object> getArgumentObjects(List<Class<?>> argumentClasses, Map<String, String> argumentValues)
    throws TestsigmaInvalidParameterDataException {
    List<Object> argumentObjects = new ArrayList<>();
    if (argumentClasses.size() > 0) {
      for (int i = 0; i < argumentValues.size(); i++) {
        String argumentKey = "arg" + i;
        try {
          if (argumentValues.get(argumentKey) != null) {
            if (argumentClasses.get(i).equals(String.class)) {
              argumentObjects.add(argumentValues.get(argumentKey));
            } else {
              argumentObjects.add(objectMapperService.parseJson((String) argumentValues.get(argumentKey), argumentClasses.get(i)));
            }

          } else {
            argumentObjects.add(argumentClasses.get(i).cast(argumentValues.get(argumentKey)));
          }

        } catch (Exception e) {
          throw new TestsigmaInvalidParameterDataException(ErrorCodes.INVALID_PARAMETER_FORMAT,
            AutomatorMessages.getMessage(AutomatorMessages.EXCEPTION_INVALID_PARAMETER_FORMAT,
              argumentValues.get(argumentKey), i + 1), "");
        }
      }
    }
    return argumentObjects;
  }

  protected Class<?> loadClass(String className, String classPackage) throws ClassNotFoundException {
    return Class.forName(classPackage + "." + className);
  }
}
