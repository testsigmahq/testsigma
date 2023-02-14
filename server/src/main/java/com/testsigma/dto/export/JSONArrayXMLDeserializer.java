package com.testsigma.dto.export;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JSONArrayXMLDeserializer extends JsonDeserializer<JSONArray> {

  @Override
  public JSONArray deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
    TreeNode treeNode = jsonParser.getCodec().readTree(jsonParser);
    String data = "[]";
    if (treeNode.isArray()) {
      data = treeNode.toString();
    } else if (treeNode.get("ArrayList") != null && treeNode.get("ArrayList").get("item") != null) {
      List<Map<String, Object>> arrayList = new ArrayList<>();
      JSONObject object = new JSONObject(treeNode.get("ArrayList").get("item").toString());
      arrayList.add(object.toMap());
      return new JSONArray(new ObjectMapper().writeValueAsString(arrayList));
    } else if (treeNode.get("ArrayList") == null && treeNode.toString().length() > 2) {
      data = new ObjectMapper().readValue(treeNode.toString(), String.class);
    }
    return new JSONArray(data);
  }
}
