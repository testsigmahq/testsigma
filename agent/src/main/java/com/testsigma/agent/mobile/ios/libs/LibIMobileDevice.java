/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.mobile.ios.libs;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;

import java.nio.IntBuffer;

public interface LibIMobileDevice extends Library {
  String JNA_LIBRARY_NAME = "imobiledevice.6";
  String MAC_LIBRARY_NAME = "libimobiledevice.6";
  String WIN_LIBRARY_NAME = "libimobiledevice";
  String LINUX_LIBRARY_NAME = "libimobiledevice-1.0";

  int idevice_get_device_list(PointerByReference devices, IntBuffer count);

  int idevice_device_list_free(Pointer devices);

  interface idevice_event_cb_t extends Callback {
    void apply(IdeviceEvent event, Pointer user_data);
  }

  class IdeviceEvent extends Structure {
    public int event;
    public Pointer uuid;
    public int conn_type;
  }

}
