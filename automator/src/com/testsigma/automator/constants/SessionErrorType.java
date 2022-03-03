package com.testsigma.automator.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SessionErrorType {
  LAB_MINUTES_EXCEEDED,
  PLATFORM_OS_NOT_SUPPORTED,
  PLATFORM_BROWSER_VERSION_NOT_SUPPORTED,
  NO_PARALLEL_RUN;
}
