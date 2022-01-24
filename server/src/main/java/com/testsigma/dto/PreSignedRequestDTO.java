package com.testsigma.dto;

import com.amazonaws.HttpMethod;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class PreSignedRequestDTO {
  private String name;
  private String key;
  private Long accountId;
  private Timestamp expiration;
  private HttpMethod method;
}
