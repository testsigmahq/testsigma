/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.mobile.android;

import com.android.ddmlib.MultiLineReceiver;

import java.util.Arrays;
import java.util.List;

final class ShellOutputReceiver extends MultiLineReceiver {
  private final List<String> outputList;

  ShellOutputReceiver(List<String> outputList) {
    this.outputList = outputList;
  }

  public final boolean isCancelled() {
    return false;
  }

  public final void processNewLines(String[] newLines) {
    this.outputList.addAll(Arrays.asList(newLines));
  }
}
