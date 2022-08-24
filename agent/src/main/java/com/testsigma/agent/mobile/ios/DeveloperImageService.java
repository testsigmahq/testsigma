package com.testsigma.agent.mobile.ios;

import com.testsigma.agent.config.AgentConfig;
import com.testsigma.agent.dto.IosDeveloperImageDTO;
import com.testsigma.agent.exception.TestsigmaException;
import com.testsigma.agent.http.ServerURLBuilder;
import com.testsigma.agent.http.WebAppHttpClient;
import com.testsigma.agent.mobile.MobileDevice;
import com.testsigma.agent.utils.PathUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.http.HttpResponse;
import com.testsigma.automator.mobile.ios.IosDeviceCommandExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;

@Log4j2
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DeveloperImageService {
  private final AgentConfig agentConfig;
  private final WebAppHttpClient httpClient;

  public Boolean isDeveloperImageAvailable(String deviceOsVersion) {
    log.info("Checking if developer image directory is available for osVersion - " + deviceOsVersion);
    Boolean isAvailable = Boolean.FALSE;
    File developerImagePath = developerImageDirectory(deviceOsVersion);
    if (developerImagePath.exists()) {
      File imagePath = Paths.get(developerImagePath.getAbsolutePath(), "DeveloperDiskImage.dmg").toFile();
      File imageSigPath = Paths.get(developerImagePath.getAbsolutePath(), "DeveloperDiskImage.dmg.signature").toFile();
      if (imagePath.exists() && imageSigPath.exists())
        isAvailable = Boolean.TRUE;
    }
    log.info("Developer image availability - " + isAvailable + " , osVersion - " + deviceOsVersion);
    return isAvailable;
  }

  public File developerImageDirectory(String deviceOsVersion) {
    return Paths.get(developerImageBaseDirectory(), deviceOsVersion).toFile();
  }

  public String developerImageBaseDirectory() {
    return Paths.get(PathUtil.getInstance().getIosPath(), "DeviceSupport").toString();
  }

  public void mountDeveloperImage(MobileDevice device) throws TestsigmaException, AutomatorException {
    log.info("Trying to mount developer image onto the device");
    if (!isDeveloperImageAvailable(device.getOsVersion())) {
      IosDeveloperImageDTO iosDeveloperImageDTO = fetchDeveloperImageLinks(device.getOsVersion());
      downloadDeveloperImage(device.getOsVersion(), iosDeveloperImageDTO);
    }

    String developerImageDirectory = developerImageDirectory(device.getOsVersion()).getAbsolutePath();
    if (new File(developerImageDirectory).exists()) {
      log.info("Developer image exists at - " + developerImageDirectory);
    } else {
      log.info("Developer image could not be fetched for osVersion - " + device.getOsVersion());
    }
    IosDeviceCommandExecutor iosDeviceCommandExecutor = new IosDeviceCommandExecutor();
    Process p = iosDeviceCommandExecutor.runDeviceCommand(new String[]{"-u", device.getUniqueId(), "developer",
      developerImageDirectory}, true);
    String mountCommandOutput = iosDeviceCommandExecutor.getProcessStreamResponse(p);
    log.info("Response from mount developer image on device - " + mountCommandOutput);

    if (mountCommandOutput.contains("PairingDialogResponsePending")) {
      throw new TestsigmaException("Device is not yet paired. Triggered the trust dialogue. Please accept and try again",
        "Device is not yet paired. Triggered the trust dialogue. Please accept and try again");
    } else if (mountCommandOutput.contains("DeveloperImage already mounted")) {
      log.info("Developer image is already mounted in the device");
    } else if (mountCommandOutput.contains("DeveloperImage mounted successfully")) {
      log.info("Developer image is mounted successfully on the device");
    } else if (mountCommandOutput.contains("DeviceLocked")) {
      throw new TestsigmaException("Device is locked with a passcode. Please unlock and try again",
        "Device is locked with a passcode. Please unlock and try again");
    } else {
      throw new TestsigmaException("Unknown error while mounting developer image to the device",
        "Unknown error while mounting developer image to the device");
    }
  }

  public IosDeveloperImageDTO fetchDeveloperImageLinks(String osVersion) throws TestsigmaException {
    IosDeveloperImageDTO iosDeveloperImageDTO;
    log.info("Fetching developer image URL's from testsigma servers...");
    try {
      String authHeader = WebAppHttpClient.BEARER + " " + agentConfig.getJwtApiKey();
      HttpResponse<IosDeveloperImageDTO> response =
        httpClient
          .get(ServerURLBuilder.deviceDeveloperImageURL(this.agentConfig.getUUID(), osVersion), new TypeReference<>() {
          }, authHeader);
      log.info("Response of developer image fetch request - " + response.getStatusCode());
      if (response.getStatusCode() == HttpStatus.OK.value()) {
        iosDeveloperImageDTO = response.getResponseEntity();
      } else {
        String errorMsg = String.format("Error while fetching developer image - [%s] - [%s] ", response.getStatusCode(),
          response.getStatusMessage());
        throw new TestsigmaException(errorMsg, errorMsg);
      }
      log.info("Response from device developer image urls for os version - " + osVersion + " is - " + iosDeveloperImageDTO);
    } catch (Exception e) {
      throw new TestsigmaException(e.getMessage(), e);
    }
    return iosDeveloperImageDTO;
  }

  public void downloadDeveloperImage(String deviceOsVersion, IosDeveloperImageDTO iosDeveloperImageDTO)
    throws TestsigmaException {
    try {
      log.info("Downloading developer image files for os version - " + deviceOsVersion);

      File deviceDeveloperImageFilePath = Paths.get(developerImageBaseDirectory(), deviceOsVersion,
        "DeveloperDiskImage.dmg").toFile();
      log.info("Copying from " + iosDeveloperImageDTO.getDeveloperImageUrl() + " to " + deviceDeveloperImageFilePath);
      FileUtils.copyURLToFile(new URL(iosDeveloperImageDTO.getDeveloperImageUrl()), deviceDeveloperImageFilePath,
              (60 * 1000), (60 * 1000));

      File deviceDeveloperImageSigFilePath = Paths.get(developerImageBaseDirectory(), deviceOsVersion,
        "DeveloperDiskImage.dmg.signature").toFile();
      log.info("Copying from " + iosDeveloperImageDTO.getDeveloperImageSignatureUrl() + " to " + deviceDeveloperImageSigFilePath);
      FileUtils.copyURLToFile(new URL(iosDeveloperImageDTO.getDeveloperImageSignatureUrl()),
              deviceDeveloperImageSigFilePath, (60 * 1000), (60 * 1000));
    } catch (Exception e) {
      throw new TestsigmaException(e.getMessage(), e);
    }
  }

}
