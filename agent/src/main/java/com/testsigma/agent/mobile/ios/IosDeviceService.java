package com.testsigma.agent.mobile.ios;

import com.dd.plist.NSArray;
import com.dd.plist.NSObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.testsigma.agent.config.AgentConfig;
import com.testsigma.agent.constants.DeviceStatus;
import com.testsigma.agent.constants.MobileOs;
import com.testsigma.agent.exception.TestsigmaException;
import com.testsigma.agent.http.WebAppHttpClient;
import com.testsigma.agent.mobile.MobileDevice;
import com.dd.plist.NSDictionary;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.mobile.ios.AppInstaller;
import com.testsigma.automator.mobile.ios.IosDeviceCommandExecutor;
import com.testsigma.automator.service.ObjectMapperService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.SystemUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
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
  private final ObjectMapperService objectMapperService;

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

  public List<MobileDevice> simulatorDeviceList() throws AutomatorException, TestsigmaException {
    log.info("Fetching iOS simulator list");
    List<MobileDevice> deviceList = new ArrayList<>();
    IosDeviceCommandExecutor iosDeviceCommandExecutor = new IosDeviceCommandExecutor();
    Process p = iosDeviceCommandExecutor.runDeviceCommand(new String[]{"list-targets", "--json"}, false);
    String devicesJsonString = iosDeviceCommandExecutor.getProcessStreamResponse(p);
    String[] devices = devicesJsonString.split("\n");
    for(String deviceJson : devices) {
      JSONObject deviceJsonObject = getSimulatorProperties(deviceJson);
      if(deviceJsonObject.getString("state").equals(DeviceStatus.BOOTED.getStatus()) && deviceJsonObject.getString("type").equals("simulator")) {
        try {
          MobileDevice device = getSimulatorDevice(deviceJsonObject.getString("udid"));
          deviceList.add(device);
        } catch(Exception e) {
          log.error(e.getMessage());
        }
      }
    }
    return deviceList;
  }

  public JSONObject getDeviceProperties(String uniqueId) throws TestsigmaException {
    try {
      log.info("Fetching device properties for device uniqueID - " + uniqueId);
      IosDeviceCommandExecutor iosDeviceCommandExecutor = new IosDeviceCommandExecutor();
      Process p = iosDeviceCommandExecutor.runDeviceCommand(new String[]{"-u", uniqueId, "info", "--json"}, true);
      String devicePropertiesJsonString = iosDeviceCommandExecutor.getProcessStreamResponse(p);
      log.info("Fetched device properties for device - " + uniqueId + ", properties - " + devicePropertiesJsonString);
      JSONObject devicePropertiesJson = new JSONObject(devicePropertiesJsonString);
      log.info("Fetched device properties for device - " + uniqueId + ", json format - " + devicePropertiesJson);
      return devicePropertiesJson;
    } catch(Exception e) {
      throw new TestsigmaException(e.getMessage());
    }
  }

  public JSONObject getSimulatorProperties(String deviceJson) throws TestsigmaException {
    try {
      return new JSONObject(deviceJson);
    } catch (Exception e) {
      throw new TestsigmaException(e.getMessage());
    }
  }

  public void setupWda(MobileDevice device) throws TestsigmaException, AutomatorException {
    log.info("Setting up WDA on device - " + device.getName());
    try {
      wdaService.installWdaToDevice(device);
      if(device.getIsEmulator()) {
        wdaService.installXCTestToDevice(device);
      }
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

  public String installApp(MobileDevice device, String appUrl, Boolean isEmulator) throws AutomatorException {
    return new AppInstaller(httpClient).installApp(device.getName(), device.getUniqueId(), appUrl, isEmulator);
  }

  public MobileDevice getSimulatorDevice(String udid) throws AutomatorException, TestsigmaException {
    IosDeviceCommandExecutor iosDeviceCommandExecutor = new IosDeviceCommandExecutor();
    Process p = iosDeviceCommandExecutor.runDeviceCommand(new String[]{"describe", "--udid", udid, "--json"}, false);
    String deviceDescriptionJson = iosDeviceCommandExecutor.getProcessStreamResponse(p);
    JSONObject device = getSimulatorProperties(deviceDescriptionJson);
    MobileDevice mobileDevice = new MobileDevice();
    mobileDevice.setOsName(MobileOs.IOS);
    mobileDevice.setUniqueId(device.getString("udid"));
    mobileDevice.setName(device.getString("name"));
    mobileDevice.setOsVersion(device.getString("os_version").split(" ")[1]);
    mobileDevice.setApiLevel(mobileDevice.getOsVersion());
    mobileDevice.setAbi(device.getString("architecture"));
    mobileDevice.setIsOnline(device.getString("state").equals(DeviceStatus.BOOTED.getStatus()));
    mobileDevice.setProductModel(device.isNull("model") ? "-" : device.get("model").toString());
    mobileDevice.setIsEmulator(device.getString("target_type").equals("simulator"));
    mobileDevice.setScreenHeight(device.getJSONObject("screen_dimensions").getInt("height"));
    mobileDevice.setScreenWidth(device.getJSONObject("screen_dimensions").getInt("width"));
    return mobileDevice;
  }

}
