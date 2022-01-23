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

public interface LibUsbMuxd extends Library {
  String MAC_LIBRARY_NAME = "libusbmuxd.6";
  String WIN_LIBRARY_NAME = "usbmuxd";
  String LINUX_LIBRARY_NAME = "libusbmuxd-2.0";
}
