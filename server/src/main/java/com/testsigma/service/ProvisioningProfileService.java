package com.testsigma.service;

import com.testsigma.config.StorageServiceFactory;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.model.ProvisioningProfile;
import com.testsigma.model.ProvisioningProfileDevice;
import com.testsigma.model.ProvisioningProfileStatus;
import com.testsigma.model.TestDevice;
import com.testsigma.repository.ProvisioningProfileRepository;
import com.testsigma.tasks.ReSignTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class ProvisioningProfileService {
  private final ProvisioningProfileRepository provisioningProfileRepository;
  private final CertificateService certificateService;
  private final ProvisioningProfileParserService profileParserService;
  private final ProvisioningProfileDeviceService provisioningProfileDeviceService;
  private final TestDeviceService testDeviceService;
  private final WebApplicationContext webApplicationContext;
  private final StorageServiceFactory storageServiceFactory;

  public Page<ProvisioningProfile> findAll(Specification<ProvisioningProfile> spec, Pageable pageable) {
    return this.provisioningProfileRepository.findAll(spec, pageable);
  }

  public List<ProvisioningProfile> findAll() {
    return this.provisioningProfileRepository.findAll();
  }

  public ProvisioningProfile find(Long id) throws ResourceNotFoundException {
    ProvisioningProfile provisioningProfile = this.provisioningProfileRepository.findById(id).orElseThrow(
      () -> new ResourceNotFoundException("Profile missing with id:" + id));
    certificateService.setPreSignedURLs(provisioningProfile);
    return provisioningProfile;
  }

  public ProvisioningProfile create(ProvisioningProfile provisioningProfile) {
    provisioningProfile.setStatus(ProvisioningProfileStatus.CSR_REQUESTED);
    provisioningProfile = this.provisioningProfileRepository.save(provisioningProfile);
    try {
      log.info(String.format("Generating CSR for provisioningProfile [%s] - [%s]", provisioningProfile.getId(),
        provisioningProfile.getName()));
      String csrPathPrefix = certificateService.s3Prefix(provisioningProfile.getId());
      String csrFileName = +provisioningProfile.getId() + CertificateService.CSR_FILE_SUFFIX;
      String privateKeyFileName = provisioningProfile.getId()
        + CertificateService.PRIVATE_KEY_FILE_SUFFIX;
      String csrS3FileName = csrPathPrefix + CertificateService.CSR_FILE_SUFFIX;
      String privateKeyS3FileName = csrPathPrefix + CertificateService.PRIVATE_KEY_FILE_SUFFIX;

      File csrFile = File.createTempFile(csrFileName, CertificateService.CSR_EXTENSION);
      File privateKeyFile = File.createTempFile(privateKeyFileName, CertificateService.PEM_EXTENSION);
      certificateService.writeCSR(csrFile, privateKeyFile);
      log.info(String.format("Uploading CSR for provisioningProfile [%s] - [%s]", provisioningProfile.getId(),
        provisioningProfile.getName()));
      storageServiceFactory.getStorageService().addFile(csrS3FileName + CertificateService.CSR_EXTENSION, csrFile);
      storageServiceFactory.getStorageService().addFile(privateKeyS3FileName + CertificateService.PEM_EXTENSION, privateKeyFile);
      provisioningProfile.setStatus(ProvisioningProfileStatus.AWAITING_FOR_CERTIFICATE);
      provisioningProfile = this.provisioningProfileRepository.save(provisioningProfile);
      certificateService.setPreSignedURLs(provisioningProfile);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return provisioningProfile;
  }

  public ProvisioningProfile update(ProvisioningProfile provisioningProfile) throws TestsigmaException {
    try {
      MultipartFile cer = provisioningProfile.getCer();
      MultipartFile provFile = provisioningProfile.getProvisioningProfile();

      if (cer != null) {
        log.info(String.format("Uploading Certificate files(cer, crt, pem) for provisioningProfile [%s] - [%s]",
          provisioningProfile.getId(), provisioningProfile.getName()));

        updateCRT(cer, provisioningProfile);
        provisioningProfile.setStatus(ProvisioningProfileStatus.AWAITING_FOR_PROVISIONING_PROFILE);
        provisioningProfile.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        provisioningProfile = this.provisioningProfileRepository.save(provisioningProfile);
      }

      if (provFile != null) {
        log.info(String.format("Uploading Certificate mobile provisioning file for provisioningProfile [%s] - [%s]",
          provisioningProfile.getId(), provisioningProfile.getName()));
        File tempProvFile = File.createTempFile(provisioningProfile.getId() + CertificateService.MOBILE_PROVISION_FILE_SUFFIX,
          CertificateService.MOBILE_PROVISION_EXTENSION);
        provFile.transferTo(tempProvFile.toPath());
        parseDeviceInfoFromProvisioningProfile(tempProvFile, provisioningProfile);
        updateProvisioningProfile(tempProvFile, provisioningProfile);
        provisioningProfile.setStatus(ProvisioningProfileStatus.READY);
        provisioningProfile.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        provisioningProfileRepository.save(provisioningProfile);
        certificateService.setPreSignedURLs(provisioningProfile);
        ReSignTask reSignTask = new ReSignTask(webApplicationContext, provisioningProfile, null);
        ReSignTaskFactory.getInstance().startTask(reSignTask);
      }
    } catch (IOException e) {
      throw new TestsigmaException(e.getMessage(), e);
    }
    return provisioningProfile;
  }

  private void checkIfDevicesIsAlreadyProvisioned(List<String> deviceUDIDs, ProvisioningProfile provisioningProfile)
    throws TestsigmaException {
    List<ProvisioningProfileDevice> conflictingDevices = provisioningProfileDeviceService
      .findAllByDeviceUDIdInAndProvisioningProfileIdNot(deviceUDIDs, provisioningProfile.getId());
    if (conflictingDevices.size() > 0) {
      List<String> conflictingDeviceUDIDs = conflictingDevices.stream()
        .map(ProvisioningProfileDevice::getDeviceUDId).collect(Collectors.toList());
      String errorMsg = "These devices are already provisioned through difference provisioning profile"
        + conflictingDeviceUDIDs + " Devices with multiple provisioning profiles are not allowed.";
      throw new TestsigmaException(errorMsg, errorMsg);
    }
  }

  private void removeProvisionedDevicesNotInProvisioningProfile(List<String> deviceUDIDs, ProvisioningProfile provisioningProfile)
    throws TestsigmaException {
    List<ProvisioningProfileDevice> existingDevices = provisioningProfileDeviceService
      .findAllByProvisioningProfileId(provisioningProfile.getId());
    if (existingDevices.size() > 0) {
      List<String> existingDeviceUDIDs = existingDevices.stream()
        .map(ProvisioningProfileDevice::getDeviceUDId).collect(Collectors.toList());
      existingDeviceUDIDs.removeAll(deviceUDIDs);
      if (existingDeviceUDIDs.size() > 0) {
        log.info("Removing existing device from provisioning profile devices - " + existingDeviceUDIDs);
        List<ProvisioningProfileDevice> removableDevices = provisioningProfileDeviceService
          .findAllByDeviceUDIdIn(existingDeviceUDIDs);
        List<Long> removedAgentDeviceIds = removableDevices.stream()
          .map(ProvisioningProfileDevice::getAgentDeviceId).collect(Collectors.toList());
        List<TestDevice> testDeviceServices = testDeviceService
          .findAllByAgentDeviceIds(removedAgentDeviceIds);
        if (testDeviceServices.size() > 0) {
          List<Long> existingExecutionIds = testDeviceServices.stream()
            .map(TestDevice::getTestPlanId).collect(Collectors.toList());
          throw new TestsigmaException("There are bellow devices removed from provision profile but have executions ::"
            + existingExecutionIds);
        }
        provisioningProfileDeviceService.deleteAllByDeviceUDIDIn(existingDeviceUDIDs);
      }
      log.info("Final list of device UUID's post cleanup - " + deviceUDIDs);
    }
  }

  private void parseDeviceInfoFromProvisioningProfile(File tempProvFile, ProvisioningProfile provisioningProfile)
    throws TestsigmaException, IOException {
    List<String> deviceUDIDs = profileParserService.parseDevices(tempProvFile);
    String teamId = profileParserService.getTeamId(tempProvFile);
    log.info("Identified devices from provisioning profile - " + deviceUDIDs);
    log.info("Identified team id from provisioning profile - " + teamId);
    provisioningProfile.setDeviceUDIDs(deviceUDIDs);
    provisioningProfile.setTeamId(teamId);
    checkIfDevicesIsAlreadyProvisioned(deviceUDIDs, provisioningProfile);
    removeProvisionedDevicesNotInProvisioningProfile(deviceUDIDs, provisioningProfile);
    provisioningProfileDeviceService.create(deviceUDIDs, provisioningProfile);
  }

  private void updateCRT(MultipartFile cer, ProvisioningProfile provisioningProfile) throws TestsigmaException {
    try {
      String profilePathPrefix = certificateService.s3Prefix(provisioningProfile.getId());
      String certificateLocalName = provisioningProfile.getId()
        + CertificateService.CERTIFICATE_FILE_SUFFIX;
      String certificateS3Name = profilePathPrefix + CertificateService.CERTIFICATE_FILE_SUFFIX;
      File cerFile = File.createTempFile(certificateLocalName, CertificateService.CERTIFICATE_CER_EXTENSION);
      File crt = File.createTempFile(certificateLocalName, CertificateService.CERTIFICATE_CRT_EXTENSION);
      File pem = File.createTempFile(certificateLocalName, CertificateService.PEM_EXTENSION);
      cer.transferTo(cerFile.toPath());

      log.info(String.format("Uploading certificate(cer) for provisioningProfile [%s] - [%s]", provisioningProfile.getId(),
        provisioningProfile.getName()));
      storageServiceFactory.getStorageService().addFile(certificateS3Name + CertificateService.CERTIFICATE_CER_EXTENSION, cerFile);

      log.info(String.format("Uploading certificate(crt) for provisioningProfile [%s] - [%s]", provisioningProfile.getId(),
        provisioningProfile.getName()));
      certificateService.writeCRT(cerFile, crt);
      storageServiceFactory.getStorageService().addFile(certificateS3Name + CertificateService.CERTIFICATE_CRT_EXTENSION, crt);

      log.info(String.format("Uploading certificate(pem) for provisioningProfile [%s] - [%s]", provisioningProfile.getId(),
        provisioningProfile.getName()));
      certificateService.writePem(crt, pem);
      storageServiceFactory.getStorageService().addFile(certificateS3Name + CertificateService.PEM_EXTENSION, pem);
    } catch (Exception e) {
      throw new TestsigmaException(e.getMessage(), e);
    }
  }

  private void updateProvisioningProfile(File provFile, ProvisioningProfile provisioningProfile) throws IOException {
    log.debug(String.format("Uploading ProvisioningProfile for provisioningProfile [%s] - [%s]",
      provisioningProfile.getId(), provisioningProfile.getName()));
    String profilePathPrefix = certificateService.s3Prefix(provisioningProfile.getId());
    storageServiceFactory.getStorageService().addFile(profilePathPrefix + CertificateService.MOBILE_PROVISION_FILE_SUFFIX
      + CertificateService.MOBILE_PROVISION_EXTENSION, provFile);
  }

  public void destroy(Long id) throws ResourceNotFoundException {
    ProvisioningProfile profile = find(id);
    this.provisioningProfileRepository.delete(profile);
  }
}
