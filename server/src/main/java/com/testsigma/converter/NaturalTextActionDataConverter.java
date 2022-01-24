/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.converter;

import com.testsigma.model.NaturalTextActionData;
import com.testsigma.service.ObjectMapperService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Log4j2
@Converter
public class NaturalTextActionDataConverter implements AttributeConverter<NaturalTextActionData, String> {
  private final static ObjectMapperService objectMapper = new ObjectMapperService();

  @Override
  public String convertToDatabaseColumn(NaturalTextActionData attribute) {
    return objectMapper.convertToJson(attribute);
  }

  @Override
  public NaturalTextActionData convertToEntityAttribute(String json) {
    if ((json == null) || (StringUtils.isBlank(json))) {
      return new NaturalTextActionData();
    }
    NaturalTextActionData data = objectMapper.parseJson(json, NaturalTextActionData.class);
    if (data == null) {
      return new NaturalTextActionData();
    }
    return data;
  }
}
