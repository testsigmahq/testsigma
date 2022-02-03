/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.converter;

import com.testsigma.service.ObjectMapperService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Map;

@Log4j2
@Converter
public class EnvironmentDataConverter implements AttributeConverter<Map<String, String>, String> {
  private static final ObjectMapperService objectMapperService = new ObjectMapperService();

  @Override
  public String convertToDatabaseColumn(Map<String, String> attribute) {
    return objectMapperService.convertToJson(attribute);
  }

  @Override
  public Map<String, String> convertToEntityAttribute(String dbData) {
    if ((dbData == null) || StringUtils.isBlank(dbData)) {
      return null;
    }
    return objectMapperService.parseJson(dbData, Map.class);
  }
}
