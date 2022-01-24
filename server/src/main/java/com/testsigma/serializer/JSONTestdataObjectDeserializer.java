/*
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 */
package com.testsigma.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;
import com.testsigma.service.ObjectMapperService;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

@Log4j2
public class JSONTestdataObjectDeserializer extends JsonDeserializer<JSONObject> {
  @Override
  public JSONObject deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
    TreeNode treeNode = jsonParser.getCodec().readTree(jsonParser);
    try {
      Map map = new ObjectMapperService().parseJson(((TextNode) treeNode).asText(), LinkedHashMap.class);
      JSONObject dataObj = new JSONObject();
      Field jsonMap = dataObj.getClass().getDeclaredField("map");
      jsonMap.setAccessible(true);
      jsonMap.set(dataObj, map);
      return dataObj;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return new JSONObject(((TextNode) treeNode).asText());
  }
}
