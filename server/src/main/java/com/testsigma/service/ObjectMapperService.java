package com.testsigma.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;

@Log4j2
@Component

public class ObjectMapperService {
  public String convertToJson(Object object) {
    if (object == null) {
      return null;
    }
    try {
      return new ObjectMapper()
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .writeValueAsString(object);
    } catch (JsonProcessingException e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  public <T> T parseJson(String json, Class<T> classObject) {
    try {
      if (StringUtils.isNotBlank(json)) {
        return new ObjectMapper()
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
          .readValue(json, classObject);
      }
      return null;
    } catch (JsonProcessingException e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  public <T> T parseJson(String json, TypeReference<T> type) {
    try {
      if (StringUtils.isNotBlank(json)) {
        if (!(json.trim().startsWith("{") || json.trim().startsWith("["))) {
          return (T) json;
        }
        return new ObjectMapper()
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
          .readValue(json, type);
      }
      return null;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  public <T> T parseJson(BufferedReader json, TypeReference<T> type) {
    try {
      if (json != null) {
        return new ObjectMapper()
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
          .readValue(json, type);
      }
      return null;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  public <T> T parseJson(InputStream is, TypeReference<T> type) {
    try {
      if (is != null) {
        return new ObjectMapper()
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
          .readValue(is, type);
      }
      return null;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  public <T> T parseJsonModel(String json, Class<T> classObject) throws Exception {
    if (StringUtils.isNotBlank(json)) {
      return new ObjectMapper()
              .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
              .readValue(json, classObject);
    }
    return null;
  }
}
