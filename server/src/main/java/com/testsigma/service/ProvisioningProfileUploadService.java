package com.testsigma.service;

import com.testsigma.config.StorageServiceFactory;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.model.ProvisioningProfile;
import com.testsigma.model.ProvisioningProfileDevice;
import com.testsigma.model.ProvisioningProfileUpload;
import com.testsigma.model.Upload;
import com.testsigma.repository.ProvisioningProfileUploadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired, @Lazy}))
public class ProvisioningProfileUploadService {
  private final ProvisioningProfileUploadRepository repository;
  private final StorageServiceFactory storageServiceFactory;
  private final UploadService uploadService;
  private final ProvisioningProfileDeviceService provisioningProfileDeviceService;

  public List<ProvisioningProfileUpload> findAllByProvisioningProfileId(Long profileId) {
    return repository.findAllByProvisioningProfileId(profileId);
  }

  public List<ProvisioningProfileUpload> findAllByUploadId(Long uploadId) {
    return repository.findAllByUploadId(uploadId);
  }

  public ProvisioningProfileUpload findByDeviceIdAndUploadId(Long deviceId, Long uploadId) {
    ProvisioningProfileDevice profile = provisioningProfileDeviceService.findByAgentDeviceId(deviceId);
    return this.repository.findByProvisioningProfileIdAndUploadId(profile.getProvisioningProfileId(), uploadId);
  }

  public void create(ProvisioningProfile profile, Upload upload) {
    ProvisioningProfileUpload profileUpload = new ProvisioningProfileUpload();
    profileUpload.setCreatedDate(new Timestamp(System.currentTimeMillis()));
    profileUpload.setUploadId(upload.getId());
    profileUpload.setProvisioningProfileId(profile.getId());
    repository.save(profileUpload);
  }

  public void remove(List<ProvisioningProfileUpload> provisioningProfileUploads) {
    repository.deleteAll(provisioningProfileUploads);
  }

  public void removeEntitiesForUpload(Upload upload) {
    List<ProvisioningProfileUpload> provisioningProfileUploads = findAllByUploadId(upload.getId());

    for (ProvisioningProfileUpload profileUpload : provisioningProfileUploads) {
      storageServiceFactory.getStorageService().deleteFile(upload.getResignedAppS3PathSuffix(profileUpload.getProvisioningProfileId()));
    }
    remove(provisioningProfileUploads);
  }

  public void removeEntitiesForProfile(ProvisioningProfile profile) throws TestsigmaException {
    List<ProvisioningProfileUpload> provisioningProfileUploads = findAllByProvisioningProfileId(profile.getId());
    for (ProvisioningProfileUpload provisioningProfileUpload : provisioningProfileUploads) {
      Upload originalUpload = uploadService.find(provisioningProfileUpload.getUploadId());
      storageServiceFactory.getStorageService().deleteFile(originalUpload.getResignedAppS3PathSuffix(provisioningProfileUpload.getProvisioningProfileId()));
    }
    remove(provisioningProfileUploads);
  }
}
