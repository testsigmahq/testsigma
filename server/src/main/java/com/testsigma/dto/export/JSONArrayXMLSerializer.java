/*
 * *****************************************************************************
 *  Copyright (C) 2023 Testsigma Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.dto.export;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JSONArrayXMLSerializer extends JsonSerializer<JSONArray> {

  @Override
  public void serialize(JSONArray jsonObject, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
    throws IOException {
    if (jsonObject == null) {
      return;
    }
    List<Map<String, String>> data = new ArrayList<>();
    jsonObject.forEach(obj -> {
      data.add(((JSONObject) obj).toMap().entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().toString())));
    });
    XmlMapper xmlMapper = new XmlMapper();
    jsonGenerator.writeRawValue(xmlMapper.writeValueAsString(data));
  }


}
