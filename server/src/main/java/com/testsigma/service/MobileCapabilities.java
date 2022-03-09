package com.testsigma.service;

import com.testsigma.config.StorageServiceFactory;
import com.testsigma.model.StorageAccessLevel;
import com.testsigma.constants.TSCapabilityType;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.tasks.PlatformAppUploader;
import com.testsigma.model.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class MobileCapabilities extends Capabilities {
  protected String fileName;
  @Autowired
  protected UploadService uploadService;
  @Autowired
  protected StorageServiceFactory storageServiceFactory;
  @Autowired
  protected StorageServiceFactory storageService;
  @Autowired
  protected ResignService resignService;
  @Autowired
  protected ProvisioningProfileUploadService provisioningProfileUploadService;
  @Autowired
  protected ProvisioningProfileService provisioningProfileService;
  @Autowired
  protected ProvisioningProfileDeviceService provisioningProfileDeviceService;
  @Autowired
  PlatformAppUploader platformAppUploader;


  public String getPreSignedUrl(TestDevice testDevice) throws ResourceNotFoundException {
    Upload upload = this.uploadService.find(Long.valueOf(testDevice.getAppUploadId()));
    Optional<URL> newPreSignedURL =
      this.storageServiceFactory.getStorageService().generatePreSignedURLIfExists(upload.getAppPath(),
        StorageAccessLevel.READ, 300
      );
    return newPreSignedURL.get().toString();
  }

  private String copyUploadToLocal(TestDevice testDevice) throws TestsigmaException {
    Upload upload = this.uploadService.find(testDevice.getAppUploadId());
    return storageServiceFactory.getStorageService().downloadToLocal(upload.getAppPath(),
      StorageAccessLevel.READ);
  }


  public void setTestsigmaLabAppCapability(TestDevice testDevice, AppPathType pathType,
                                           Integrations integrations,
                                           List<WebDriverCapability> capabilities) throws TestsigmaException {
    AppPathType appPathType = pathType;
    String platformAppId = null;
    String appLocalPath;
    if (AppPathType.USE_PATH == appPathType || AppPathType.UPLOADS == appPathType) {
      log.info("Found an APP_PATH / UPLOAD Id as capability. Uploading it and using it");
      if (testDevice.getAppUrl() != null) {
        appLocalPath = storageServiceFactory.getStorageService().downloadFromRemoteUrl(testDevice.getAppUrl());
      } else {
        appLocalPath = copyUploadToLocal(testDevice);
      }
      platformAppId = platformAppUploader.uploadAppToTestsigmaLab(
        integrations.getPassword(), appLocalPath);
      log.info("Finished uploading app, using app Id: " + platformAppId);
    } else if (AppPathType.APP_DETAILS == appPathType) {
      if (testDevice.getAppUrl() != null) {
        platformAppId = testDevice.getAppUrl();
      }
      log.info("Using External AppId as Capability: " + platformAppId);
    }
    capabilities.add(new WebDriverCapability(TSCapabilityType.APP, platformAppId));
  }

  @Override
  public void setHybridCapabilities(TestDevice testDevice,
                                    Integrations integrations,
                                    List<WebDriverCapability> capabilities)
    throws TestsigmaException {

  }

  @Override
  public void setTestsigmaLabCapabilities(TestDevice testDevice,
                                          Integrations integrations,
                                          List<WebDriverCapability> capabilities)
    throws TestsigmaException {

  }
}
