package com.testsigma.web.request.addon;

import lombok.Data;

@Data
public class ActionUsageRequest {
  private String fullyQualifiedName;
  private String externalUniqueId;
}
