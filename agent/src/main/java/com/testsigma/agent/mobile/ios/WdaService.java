package com.testsigma.agent.mobile.ios;

import com.testsigma.agent.config.AgentConfig;
import com.testsigma.agent.dto.IosWdaResponseDTO;
import com.testsigma.agent.exception.TestsigmaException;
import com.testsigma.agent.http.ServerURLBuilder;
import com.testsigma.agent.http.WebAppHttpClient;
import com.testsigma.agent.mobile.MobileDevice;
import com.fasterxml.jackson.core.type.TypeReference;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.http.HttpResponse;
import com.testsigma.automator.mobile.ios.IosDeviceCommandExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WdaService {
  private static final Integer WDA_PORT = 8100;
  private static final String WDA_BUNDLE_ID = "com.facebook.WebDriverAgentRunner.xctrunner";
  private final AgentConfig agentConfig;
  private final WebAppHttpClient httpClient;

  public void installWdaToDevice(MobileDevice device) throws TestsigmaException {
    File downloadedWdaFile = null;
    try {
      IosDeviceCommandExecutor iosDeviceCommandExecutor = new IosDeviceCommandExecutor();
      log.info("Installing WDA on device - " + device.getUniqueId());
      String wdaPresignedUrl = fetchWdaUrl(device);
      downloadedWdaFile = File.createTempFile("wda_", ".ipa");
      FileUtils.copyURLToFile(new URL(wdaPresignedUrl), downloadedWdaFile);
      log.info("Downloaded WDA to local file - " + downloadedWdaFile.getAbsolutePath());
      Process p = iosDeviceCommandExecutor.runDeviceCommand(new String[]{"-u", device.getUniqueId(), "install",
        downloadedWdaFile.getAbsolutePath()});
      String devicePropertiesJsonString = iosDeviceCommandExecutor.getProcessStreamResponse(p);
      log.info("Output from installing WDA file on the device - " + devicePropertiesJsonString);
      if (devicePropertiesJsonString.contains("ApplicationVerificationFailed")) {
        throw new TestsigmaException("Failed to install WDA on device - " + device.getUniqueId(),
          "Failed to install WDA on device - " + device.getUniqueId());
      }
    } catch (Exception e) {
      throw new TestsigmaException(e.getMessage(), e);
    } finally {
      if ((downloadedWdaFile != null) && downloadedWdaFile.exists()) {
        boolean deleted = downloadedWdaFile.delete();
        if (!deleted) {
          log.error("Error while deleting the downloaded wda.ipa file - " + downloadedWdaFile.getAbsolutePath());
        }
      }
    }
  }

  public void startWdaOnDevice(MobileDevice device) throws TestsigmaException, AutomatorException {
    try {
      log.info("Starting WDA on device - " + device.getName());
      log.info("Checking for any previously started WDA processes on device - " + device.getName());
      IosDeviceCommandExecutor iosDeviceCommandExecutor = new IosDeviceCommandExecutor();

      stopWdaOnDevice(device);
      device.setWdaExecutorService(Executors.newSingleThreadExecutor());
      device.setWdaRelayExecutorService(Executors.newSingleThreadExecutor());

      device.getWdaExecutorService().execute(() -> {
        try {
          Process p = iosDeviceCommandExecutor.runDeviceCommand(new String[]{"-u", device.getUniqueId(), "xctest",
            "-B", WDA_BUNDLE_ID});
          device.setWdaProcess(p);
        } catch (Exception e) {
          log.info(e.getMessage(), e);
        }
      });
      log.info("Putting the thread to sleep for 10 seconds so as wait for WDA process to start on device - " +
        device.getName());
      Thread.sleep(10000);

      checkWDAProcessStatus(device);

      device.getWdaRelayExecutorService().execute(() -> {
        try {
          Process p = iosDeviceCommandExecutor.runDeviceCommand(new String[]{"-u", device.getUniqueId(), "relay",
            WDA_PORT.toString(), WDA_PORT.toString()});
          device.setWdaRelayProcess(p);
        } catch (Exception e) {
          log.info(e.getMessage(), e);
        }
      });

      log.info("Putting the thread to sleep for 2 seconds so as wait for WDA relay process to start on device - " +
        device.getName());

      Thread.sleep(2000);

      checkWDARelayProcessStatus(device);
    } catch (Exception e) {
      throw new TestsigmaException(e.getMessage(), e);
    }
  }

  private void checkWDAProcessStatus(MobileDevice device) throws TestsigmaException, AutomatorException {
    IosDeviceCommandExecutor iosDeviceCommandExecutor = new IosDeviceCommandExecutor();

    if ((device.getWdaProcess() != null) && device.getWdaProcess().isAlive()) {
      log.info("Checked if the WDA process is still alive and it seems to be still running on device - " +
        device.getName());
      return;
    }
    log.info(iosDeviceCommandExecutor.getProcessStreamResponse(device.getWdaProcess()));
    throw new TestsigmaException("Unable to start WDA Process on device - " + device.getName()
      , "Unable to start WDA Process on device - " + device.getName());
  }

  private void checkWDARelayProcessStatus(MobileDevice device) throws TestsigmaException, AutomatorException {
    IosDeviceCommandExecutor iosDeviceCommandExecutor = new IosDeviceCommandExecutor();

    if ((device.getWdaRelayProcess() != null) && device.getWdaRelayProcess().isAlive()) {
      log.info("Checked if the WDA relay process is still alive and it seems to be still running on device - " +
        device.getName());
      return;
    }
    log.info(iosDeviceCommandExecutor.getProcessStreamResponse(device.getWdaRelayProcess()));
    throw new TestsigmaException("Unable to start WDA relay process on device - " + device.getName(),
      "Unable to start WDA relay process on device - " + device.getName());
  }

  public void stopWdaOnDevice(MobileDevice device) throws TestsigmaException {
    log.info("Check and stop any running WDA and WDA relay process on device - " + device.getName());

    try {
      stopWdaThreadIfRunning(device);
      stopWdaRelayThreadIfRunning(device);
    } catch (Exception e) {
      throw new TestsigmaException(e.getMessage(), e);
    }
  }

  private void stopWdaThreadIfRunning(MobileDevice device) {
    log.info("Checking if the WDA process is running for device - " + device.getName());
    try {
      ExecutorService executorService = device.getWdaExecutorService();
      Process p = device.getWdaProcess();
      if ((p != null) && p.isAlive()) {
        log.info("Stopping WDA process is still running for device - " + device.getName());
        p.destroy();
      } else {
        log.info("WDA process is not running for device - " + device.getName());
      }

      if (executorService != null && !executorService.isShutdown()) {
        log.info("Stopping WDA executor service running for device - " + device.getName());
        executorService.shutdownNow();
        executorService.awaitTermination(5, TimeUnit.SECONDS);
      }

      if ((p != null) && p.isAlive()) {
        log.info("WDA process still not stopped even after 5 seconds. Destroying it forcefully for device - " + device.getName());
        p.destroyForcibly();
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    } finally {
      device.setWdaProcess(null);
      device.setWdaExecutorService(null);
    }
  }

  private void stopWdaRelayThreadIfRunning(MobileDevice device) {
    log.info("Checking if the WDA relay process is running for device - " + device.getName());
    try {
      ExecutorService executorService = device.getWdaRelayExecutorService();
      Process p = device.getWdaRelayProcess();
      if ((p != null) && p.isAlive()) {
        log.info("Stopping WDA relay process is still running for device - " + device.getName());
        p.destroy();
      } else {
        log.info("WDA relay process is not running for device - " + device.getName());
      }

      if (executorService != null && !executorService.isShutdown()) {
        log.info("Stopping WDA relay executor service running for device - " + device.getName());
        executorService.shutdownNow();
        executorService.awaitTermination(2, TimeUnit.SECONDS);
      }

      if ((p != null) && p.isAlive()) {
        log.info("WDA relay process still not stopped even after 5 seconds. Destroying it forcefully for device - "
          + device.getName());
        p.destroyForcibly();
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    } finally {
      device.setWdaRelayProcess(null);
      device.setWdaRelayExecutorService(null);
    }
  }

  public String fetchWdaUrl(MobileDevice device) throws Exception {
    log.info("Fetching WDA presigned url for device - " + device.getName());
    String authHeader = WebAppHttpClient.BEARER + " " + agentConfig.getJwtApiKey();
    IosWdaResponseDTO iosWdaResponseDTO;
    HttpResponse<IosWdaResponseDTO> response =
      httpClient.get(ServerURLBuilder.wdaDownloadURL(this.agentConfig.getUUID(), device.getUniqueId()),
        new TypeReference<>() {
        }, authHeader);
    log.info("Response of wda presigned url fetch request - " + response.getStatusCode());
    if (response.getStatusCode() == HttpStatus.OK.value()) {
      iosWdaResponseDTO = response.getResponseEntity();
      log.info("Fetched WDA Presigned URL - " + iosWdaResponseDTO.getWdaPresignedUrl());
      return iosWdaResponseDTO.getWdaPresignedUrl();
    }
    return null;
  }
}
