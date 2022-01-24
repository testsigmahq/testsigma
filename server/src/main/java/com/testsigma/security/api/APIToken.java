/*
 * *****************************************************************************
 *  Copyright (C) 2019 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.security.api;

import com.testsigma.model.AgentType;
import com.testsigma.model.AuthenticationType;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class APIToken {
  private final String subject;
  private final AgentType agentType;
  private final String serverUuid;
  private AuthenticationType authenticationType;
}
