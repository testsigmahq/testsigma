package com.testsigma.automator.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

@Log4j2
@Component(value = "automatorObjectMapperService")
public class ObjectMapperService {
  public String convertToJson(Object object) {
    try {
      return new ObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .writeValueAsString(object);
    } catch (JsonProcessingException e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  public <T> T parseJson(String json, Class<T> classObject) {
    try {
      if (json != null) {
        return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
          .readValue(json, classObject);
      }
    } catch (JsonProcessingException e) {
      log.error(e.getMessage(), e);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
    return null;
  }

  public <T> T parseJson(String json, TypeReference<T> type) {
    try {
      if (json != null) {
        return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
          .readValue(json, type);
      }
      return null;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }


  public void writeValue(File file, Object classObject) {
    Writer writer = null;
    try {
      writer = new FileWriter(file);
      JsonFactory jsonFactory = new JsonFactory();
      jsonFactory.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
      jsonFactory.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
      ObjectMapper objectMapper = new ObjectMapper(jsonFactory);
      objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
      objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
      objectMapper.writeValue(writer, classObject);
      writer.flush();
      writer.close();
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }

  public <T> T readValue(File file, Class<T> objectClass) {
    try {
      return new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .readValue(file, objectClass);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
    return null;
  }
}
