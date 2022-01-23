package com.testsigma.dto;

import lombok.Data;

@Data
public class ServerDetailsDTO {

  private String serverVersion;
  private String serverIp;
  private String[] testsigmaLabIP;

}
