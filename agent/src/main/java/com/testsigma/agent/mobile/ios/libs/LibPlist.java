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

public interface LibPlist extends Library {
  String MAC_LIBRARY_NAME = "libplist.3";
  String WIN_LIBRARY_NAME = "plist";
  String LINUX_LIBRARY_NAME = "libplist-2.0";
}
