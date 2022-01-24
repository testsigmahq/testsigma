/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.request;

import com.testsigma.automator.actions.mobile.MobileElement;
import lombok.Data;

@Data
public class SendKeysRequest {
  MobileElement mobileElement;
  String keys;
}
