/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.converter;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
@Log4j2
public class JSONObjectConverter implements AttributeConverter<JSONObject, String> {

  public static synchronized JSONObject toJsonObject(String data) {
    try {
      if ((data == null) || (StringUtils.isBlank(data))) {
        return null;
      }
      return new JSONObject(data);
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
      return null;
    }
  }

  @Override
  public String convertToDatabaseColumn(JSONObject attribute) {
    if (attribute == null)
      return null;
    return attribute.toString();
  }

  @Override
  public JSONObject convertToEntityAttribute(String dbData) {
    try {
      if ((dbData == null) || (StringUtils.isBlank(dbData))) {
        return null;
      }
      return new JSONObject(dbData);
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
      return null;
    }
  }
}
