package com.testsigma.automator.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StorageType {
  ON_PREMISE, AWS_S3, AZURE_BLOB, TESTSIGMA
}
