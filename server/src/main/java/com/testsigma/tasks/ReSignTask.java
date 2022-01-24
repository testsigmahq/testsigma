package com.testsigma.tasks;

import com.testsigma.model.ProvisioningProfile;
import com.testsigma.model.Upload;
import com.testsigma.model.UploadType;
import com.testsigma.service.ProvisioningProfileService;
import com.testsigma.service.ProvisioningProfileUploadService;
import com.testsigma.service.ResignService;
import com.testsigma.service.UploadService;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.context.WebApplicationContext;

@Log4j2
@Data
public class ReSignTask implements Runnable {
  private final WebApplicationContext webApplicationContext;
  private final ProvisioningProfileUploadService profileUploadService;
  private final ProvisioningProfileService provisioningProfileService;
  private final ResignService resignService;
  private final ProvisioningProfile profile;
  private final UploadService uploadService;
  private final Upload upload;

  public ReSignTask(WebApplicationContext webApplicationContext,
                    ProvisioningProfile profile, Upload upload) {
    super();
    this.webApplicationContext = webApplicationContext;
    this.profile = profile;
    this.upload = upload;
    this.profileUploadService = webApplicationContext.getBean(ProvisioningProfileUploadService.class);
    this.resignService = webApplicationContext.getBean(ResignService.class);
    this.uploadService = webApplicationContext.getBean(UploadService.class);
    this.provisioningProfileService = webApplicationContext.getBean(ProvisioningProfileService.class);
  }

  public void run() {
    if (upload != null) {
      resignUploadForAllProfiles();
    } else {
      resignAllUploads();
    }
  }

  private void resignAllUploads() {
    try {
      log.info(String.format("Resign Upload Task Started For Provision Profile [%s] - [%s] ", profile.getId(),
        profile.getName()));
      resignService.reSignWda(profile);
      resignService.reSignAllUploads(profile,
        uploadService.findAllByType(UploadType.IPA));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  private void resignUploadForAllProfiles() {
    try {
      resignService.reSignForAllProfiles(upload, provisioningProfileService.findAll());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  public Long getId() {
    return (upload != null) ? upload.getId() : profile.getId();
  }
}
