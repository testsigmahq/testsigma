package com.testsigma.agent.mobile.ios;

import com.testsigma.agent.config.AgentConfig;
import com.testsigma.agent.exception.TestsigmaException;
import com.testsigma.agent.http.WebAppHttpClient;
import com.testsigma.agent.mobile.MobileDevice;
import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.mobile.ios.AppInstaller;
import com.testsigma.automator.mobile.ios.IosDeviceCommandExecutor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Log4j2
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IosDeviceService {

  private static int tag = 0;
  private final AgentConfig agentConfig;
  private final WebAppHttpClient httpClient;
  private final WdaService wdaService;

  public static int nextTag() {
    return (tag++);
  }

  public UsbMuxSocket createConnection() {
    return UsbMuxSocket.getSocketInstance(IosDeviceService.nextTag());
  }

  public void closeConnection(UsbMuxSocket usbMuxSocket) {
    usbMuxSocket.close();
  }

  private NSDictionary sendRecv(UsbMuxSocket usbMuxSocket, Map<String, Object> payload) throws UsbMuxReplyException,
    UsbMuxException {
    return usbMuxSocket.sendRecvPacket(payload);
  }

  public List<Device> deviceList() throws UsbMuxException {
    UsbMuxSocket usbMuxSocket = null;
    log.info("Fetching iOS device list");
    try {
      usbMuxSocket = createConnection();
      Map<String, Object> deviceListPayload = new HashMap<>();
      deviceListPayload.put("MessageType", "ListDevices");
      List<Device> deviceList = new ArrayList<>();
      NSDictionary devices = sendRecv(usbMuxSocket, deviceListPayload);
      log.info(devices.toXMLPropertyList());
      NSArray deviceArray = (NSArray) devices.get("DeviceList");
      for (NSObject deviceObject : deviceArray.getArray()) {
        Device device = buildDevice((NSDictionary) deviceObject);
        log.info("Ios Device detected - " + device);
        if (device.getConnectionType().equals("USB")) {
          deviceList.add(device);
        }
      }
      return deviceList;
    } catch (UsbMuxReplyException e) {
      throw new UsbMuxException(e.getMessage(), e);
    } finally {
      if (usbMuxSocket != null) {
        closeConnection(usbMuxSocket);
      }
    }

  }

  private Device buildDevice(NSDictionary dico) {
    Device deviceAttachMessage = new Device();
    NSDictionary properties = (NSDictionary) dico.get("Properties");
    if (properties != null) {
      deviceAttachMessage.serialNumber = properties.get("SerialNumber").toString();
      deviceAttachMessage.connectionType = properties.get("ConnectionType").toString();
      deviceAttachMessage.deviceId = Integer.valueOf(properties.get("DeviceID").toString());
      if (deviceAttachMessage.connectionType.equals("USB")) {
        deviceAttachMessage.locationId = properties.get("LocationID").toString();
        deviceAttachMessage.productId = properties.get("ProductID").toString();
      }
    }
    return deviceAttachMessage;
  }

  public JSONObject getDeviceProperties(String uniqueId) throws TestsigmaException {
    try {
      log.info("Fetching device properties for device uniqueID - " + uniqueId);
      IosDeviceCommandExecutor iosDeviceCommandExecutor = new IosDeviceCommandExecutor();
      Process p = iosDeviceCommandExecutor.runDeviceCommand(new String[]{"-u", uniqueId, "info", "--json"});
      String devicePropertiesJsonString = iosDeviceCommandExecutor.getProcessStreamResponse(p);
      log.info("Fetched device properties for device - " + uniqueId + ", properties - " + devicePropertiesJsonString);
      JSONObject devicePropertiesJson = new JSONObject(devicePropertiesJsonString);
      log.info("Fetched device properties for device - " + uniqueId + ", json format - " + devicePropertiesJson);
      return devicePropertiesJson;
    } catch (Exception e) {
      throw new TestsigmaException(e.getMessage());
    }
  }

  public void setupWda(MobileDevice device) throws TestsigmaException, AutomatorException {
    log.info("Setting up WDA on device - " + device.getName());
    try {
      wdaService.installWdaToDevice(device);
      wdaService.startWdaOnDevice(device);
    } catch (Exception e) {
      log.error("Error while setting up wda and starting it. Error - ");
      log.error(e.getMessage(), e);
      cleanupWda(device);
      throw new TestsigmaException(e.getMessage(), e);
    }
  }

  public void cleanupWda(MobileDevice device) {
    log.info("Cleaning up WDA on device - " + device.getName());
    try {
      wdaService.stopWdaOnDevice(device);
    } catch (TestsigmaException e) {
      log.error(e.getMessage(), e);
    }
  }

  public String installApp(MobileDevice device, String appUrl) throws AutomatorException {
    return new AppInstaller(httpClient).installApp(device.getName(), device.getUniqueId(), appUrl);
  }
}
