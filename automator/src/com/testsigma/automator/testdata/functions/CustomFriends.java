package com.testsigma.automator.testdata.functions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Log4j2
public class CustomFriends {

  private Map<String, List<String>> friends = new HashMap<>();

  public CustomFriends() {
    String toReturn = "{\"quotes\":[\"Lorem Ipsum dolor sit amet\",\"consectetur adipiscing elit\"," +
      "\"sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam\"," +
      "\"quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat\"," +
      "\"Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur\"," +
      "\"Excepteur sint occaecat cupidatat non proident\"," +
      "\"sunt in culpa qui officia deserunt mollit anim id est laborum\"]}";

    try {
      friends = new ObjectMapper().readValue(toReturn, new TypeReference<Map<String, List<String>>>() {
      });
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }

  public String quote() {
    List<String> quotes = friends.get("quotes");
    return quotes.get(getRandomInt(0, quotes.size() - 1));
  }

  private int getRandomInt(int min, int max) {
    Random ran = new Random();
    int randomInt = min + ran.nextInt(max - min + 1);
    return randomInt;
  }

}
