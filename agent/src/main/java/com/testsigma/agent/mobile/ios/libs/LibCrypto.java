/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.mobile.ios.libs;

import com.sun.jna.Library;

public interface LibCrypto extends Library {
  String MAC_LIBRARY_NAME = "libcrypto.1.1";
  String WIN_LIBRARY_NAME = "libcrypto-1_1";
}
