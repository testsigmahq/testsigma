package com.testsigma.web.request;

import lombok.Data;

import java.sql.Timestamp;
import java.time.Instant;

@Data
public class ServerRequest {
  private String serverUuid;
  private String serverVersion;
  private String serverOs;
  private Boolean consent;
  private Boolean consentRequestDone;
  private Timestamp lastUpTime = Timestamp.from(Instant.now());
}
