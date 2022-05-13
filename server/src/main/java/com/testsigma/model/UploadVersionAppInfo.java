package com.testsigma.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UploadVersionAppInfo {
    String appActivity;
    String packageName;
    String appVersion;
    String bundleId;
}
