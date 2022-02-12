package com.testsigma.model;

public enum AddonApplicationType {
  WEB_APPLICATION, MOBILE_WEB_APPLICATION, iOS_NATIVE, ANDROID_NATIVE;

  public WorkspaceType getWorkspaceType() {
    switch (this) {
      case WEB_APPLICATION:
        return WorkspaceType.WebApplication;
      case MOBILE_WEB_APPLICATION:
        return WorkspaceType.MobileWeb;
      case ANDROID_NATIVE:
        return WorkspaceType.AndroidNative;
      default:
        return WorkspaceType.IOSNative;
    }
  }
}
