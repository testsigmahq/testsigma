package com.testsigma.automator.webservices;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class WebserviceResponse {

  public static String MATCH_TYPE = "match_type";
  public static String DATA = "data";

  private int status;
  private Map<String, String> headers; //{"match_type":"FULL", "data":{}}
  private String content;  //{"match_type":"FULL", "data":{}}

}
