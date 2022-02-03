package com.testsigma.agent.dto;

import lombok.Data;

import java.net.URL;

@Data
public class AgentWebServerConfigDTO {
  private URL certificatePresignedURL;
  private byte[] certificate;
  private byte[] key;
  private int httpPort;
  private int httpsPort;
}
