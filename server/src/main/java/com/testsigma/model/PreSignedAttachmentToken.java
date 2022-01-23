package com.testsigma.model;

import com.amazonaws.HttpMethod;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class PreSignedAttachmentToken {
  private String name;
  private String key;
  private Timestamp expiration;
  private HttpMethod method;
}
