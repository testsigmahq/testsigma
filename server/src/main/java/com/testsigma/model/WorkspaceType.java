/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum WorkspaceType {
  WebApplication,
  MobileWeb,
  AndroidNative,
  IOSWeb,
  IOSNative,
  Rest;

  public static boolean isWebApp(WorkspaceType appType) {
    return appType == WebApplication || appType == MobileWeb;
  }

  public boolean isMobile() {
    return (this == MobileWeb) || (this == AndroidNative) || (this == IOSNative);
  }

  public boolean isWeb() {
    return this == WebApplication;
  }

  public boolean isRest() {
    return this == Rest;
  }

}
