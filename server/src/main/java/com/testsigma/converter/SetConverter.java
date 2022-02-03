/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.testsigma.service.ObjectMapperService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import java.util.List;

@Log4j2
public abstract class SetConverter<T> implements AttributeConverter<List<T>, String> {
  private static final ObjectMapperService objectMapperService = new ObjectMapperService();

  @Override
  public String convertToDatabaseColumn(List<T> attribute) {
    return objectMapperService.convertToJson(attribute);
  }

  @Override
  public List<T> convertToEntityAttribute(String dbData) {
    if ((dbData == null) || StringUtils.isBlank(dbData)) {
      return null;
    }
    return objectMapperService.parseJson(dbData, new TypeReference<List<T>>() {
    });
  }
}
