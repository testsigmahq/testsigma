package com.testsigma.web.request.kibbutz;

import lombok.Data;

@Data
public class ActionUsageRequest {
  private String fullyQualifiedName;
  private String externalUniqueId;
}
