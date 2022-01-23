package com.testsigma.agent.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.SystemUtils;

@Getter
@RequiredArgsConstructor
public enum AgentOs {
  WINDOWS("Windows"),
  MACOSX("Mac OS X"),
  LINUX("Linux");
  private final String name;

  public static AgentOs getLocalAgentOs() {
    if (SystemUtils.IS_OS_WINDOWS) {
      return AgentOs.WINDOWS;
    } else if (SystemUtils.IS_OS_MAC_OSX) {
      return AgentOs.MACOSX;
    } else if (SystemUtils.IS_OS_LINUX) {
      return AgentOs.LINUX;
    } else {
      return null;
    }
  }
}
