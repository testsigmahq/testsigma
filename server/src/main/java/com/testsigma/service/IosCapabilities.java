package com.testsigma.service;


import com.testsigma.model.StorageAccessLevel;
import com.testsigma.constants.TSCapabilityType;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.model.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Service
public class IosCapabilities extends MobileCapabilities {

  @Override
  public void setTestsigmaLabCapabilities(TestDevice testDevice,
                                          Integrations integrations, List<WebDriverCapability> capabilities)
    throws TestsigmaException {
    if (testDevice.getAppPathType() != null)
      setTestsigmaLabAppCapability(testDevice, testDevice.getAppPathType(),
              integrations, capabilities);
  }

  public void setHybridAppCapability(TestDevice testDevice, AppPathType appPathType,
                                     List<WebDriverCapability> capabilities)
    throws TestsigmaException {
    if (AppPathType.UPLOADS == appPathType) {
      capabilities.add(new WebDriverCapability(TSCapabilityType.APP,
        getIosResignedPresignedUrlFromUpload(testDevice)));
    } else if (AppPathType.USE_PATH == appPathType) {
      capabilities.add(new WebDriverCapability(TSCapabilityType.APP,
        getIosResignedPresignedUrlFromPath(testDevice)));
    } else if (AppPathType.APP_DETAILS == appPathType) {
      capabilities.add(new WebDriverCapability(TSCapabilityType.BUNDLE_ID, testDevice.getAppBundleId()));
    }
  }

  @Override
  public void setHybridCapabilities(TestDevice testDevice,
                                    Integrations integrations,
                                    List<WebDriverCapability> capabilities)
    throws TestsigmaException {
    capabilities.add(new WebDriverCapability(TSCapabilityType.AUTOMATION_NAME, TSCapabilityType.XCUI_TEST));
    capabilities.add(new WebDriverCapability(TSCapabilityType.WDA_URL, TSCapabilityType.WDA_URL_VALUE));
    if (testDevice.getAppPathType() != null)
      setHybridAppCapability(testDevice, testDevice.getAppPathType(), capabilities);
  }

  private String getIosResignedPresignedUrlFromUpload(TestDevice testDevice)
    throws TestsigmaException {
    Upload upload = uploadService.find(Long.valueOf(testDevice.getAppUploadId()));
    ProvisioningProfileUpload profileUpload = provisioningProfileUploadService
      .findByDeviceIdAndUploadId(testDevice.getDeviceId(), upload.getId());
    ProvisioningProfile provisioningProfile = provisioningProfileService.find(profileUpload.getProvisioningProfileId());
    return storageServiceFactory.getStorageService().generatePreSignedURL(upload.getResignedAppS3PathSuffix(provisioningProfile.getId()),
      StorageAccessLevel.READ, 300).toString();
  }

  private String getIosResignedPresignedUrlFromPath(TestDevice testDevice)
    throws TestsigmaException {
    String publicURLString = testDevice.getAppBundleId();
    if (StringUtils.isEmpty(publicURLString) && StringUtils.isNotEmpty(testDevice.getAppUrl())) {
      publicURLString = testDevice.getAppUrl();
    }
    setFileName(publicURLString);
//    String resignedPathSuffix = "/uploads/resigned/test_plans/" +
//      testDeviceSettings.getExecutionRunId() + "/environment/" + testDeviceSettings.getEnvRunId() + "/" + fileName;
//    ProvisioningProfileDevice profileDevice = provisioningProfileDeviceService.findByAgentDeviceId(
//      testDevice.getDeviceId());
//    ProvisioningProfile profile = provisioningProfileService.find(profileDevice.getProvisioningProfileId());
//    resignService.resignPublicUrlApp(profile, publicURLString, resignedPathSuffix);
//    publicURLString = storageServiceFactory.getStorageService().generatePreSignedURL(resignedPathSuffix, AccessLevel.READ,
//      180).toString();
    return publicURLString;
  }

  public void setFileName(String appRemoteUrl) throws TestsigmaException {
    try {
      URL url = new URL(appRemoteUrl);
      this.fileName = FilenameUtils.getName(url.getPath());
    } catch (MalformedURLException ex) {
      throw new TestsigmaException(ex);
    }
  }
}
