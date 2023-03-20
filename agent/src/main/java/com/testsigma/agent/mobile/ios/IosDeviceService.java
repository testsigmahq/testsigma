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
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
  private final IosDeviceCommandExecutor iosDeviceCommandExecutor;
  private final Map<String, String> modelMap = new ConcurrentHashMap<>();

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
    Process p = iosDeviceCommandExecutor.runDeviceCommand(new String[]{"list", "devices", "available", "--json"}, false);
    String devicesJsonString = iosDeviceCommandExecutor.getProcessStreamResponse(p);
    try {
      JSONObject devices = new JSONObject(devicesJsonString);
      deviceList = parseSimulatorDevices(devices);
    } catch (Exception e) {
      log.error("Failed to fetch simulators", e);
    }
    return deviceList;
  }

  public JSONObject getDeviceProperties(String uniqueId) throws TestsigmaException {
    try {
      log.info("Fetching device properties for device uniqueID - " + uniqueId);
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

  public String installApp(MobileDevice device, String appUrl, Boolean isEmulator) throws AutomatorException {
    return new AppInstaller(httpClient).installApp(device.getName(), device.getUniqueId(), appUrl, isEmulator);
  }

  private List<MobileDevice> parseSimulatorDevices(JSONObject devices) {
    List<MobileDevice> simulatorDevices = new ArrayList<>();
    JSONObject devicesByVersion = devices.getJSONObject("devices");
    Set<String> versions = devicesByVersion.keySet();
    for (String version : versions) {
      JSONArray deviceList = devicesByVersion.getJSONArray(version);
      for (int i = 0; i < deviceList.length(); i++) {
        JSONObject device = deviceList.getJSONObject(i);
        if (!device.getString("state").equals(DeviceStatus.BOOTED.getStatus())) {
          continue;
        }
        MobileDevice mobileDevice = new MobileDevice();
        mobileDevice.setOsName(MobileOs.IOS);
        mobileDevice.setUniqueId(device.getString("udid"));
        mobileDevice.setName(device.getString("name"));
        mobileDevice.setOsVersion(parseOsVersion(version));
        mobileDevice.setApiLevel(mobileDevice.getOsVersion());
        mobileDevice.setIsOnline(true);
        mobileDevice.setProductModel(fetchProductModel(device.getString("deviceTypeIdentifier")));
        mobileDevice.setIsEmulator(true);
        Map<String, Integer> dimensions = fetchDimensions(mobileDevice.getUniqueId());
        mobileDevice.setScreenHeight(dimensions.get("height"));
        mobileDevice.setScreenWidth(dimensions.get("width"));
        simulatorDevices.add(mobileDevice);
      }
    }
    return simulatorDevices;
  }

  private Map<String, Integer> fetchDimensions(String udid) {
    Integer width = null;
    Integer height = null;
    Map<String, Integer> dimensions = new HashMap<>();
    try {
      Process process = iosDeviceCommandExecutor.runDeviceCommand(new String[]{"io", udid, "enumerate"}, false);
      String ioOutput = iosDeviceCommandExecutor.getProcessStreamResponse(process);
      String[] lines = ioOutput.split("\n");
      int i;
      for (i = 0; i < lines.length; i++) {
        if (lines[i].contains("IOSurface port:")) break;
      }
      for (i = i + 1; i < lines.length; i++) {
        String line = lines[i].trim();
        if (line.startsWith("Port:")) {
          break;
        }
        int dimension = Integer.parseInt(line.substring(line.lastIndexOf('=') + 1).trim());
        if (line.startsWith("width ")) {
          width = dimension;
        } else if (line.startsWith("height ")) {
          height = dimension;
        }
        if (height != null && width != null) break;
      }
    } catch (Exception e) {
      log.error("Could not fetch screen dimensions", e);
    }
    dimensions.put("width", width);
    dimensions.put("height", height);
    return dimensions;
  }

  private String fetchProductModel(String deviceTypeIdentifier) {
    if (!modelMap.containsKey(deviceTypeIdentifier)) {
      fetchSimulatorModels();
    }
    return modelMap.getOrDefault(deviceTypeIdentifier, "-");
  }

  private void fetchSimulatorModels() {
    try {
      Process process = iosDeviceCommandExecutor.runDeviceCommand(new String[]{"list", "devicetypes", "--json"}, false);
      String deviceTypesOutput = iosDeviceCommandExecutor.getProcessStreamResponse(process);
      JSONArray deviceTypes = new JSONObject(deviceTypesOutput).getJSONArray("devicetypes");
      for (int i = 0; i < deviceTypes.length(); i++) {
        JSONObject deviceType = deviceTypes.getJSONObject(i);
        modelMap.put(deviceType.getString("identifier"), deviceType.getString("name"));
      }
    } catch (Exception e) {
      log.error("Could not fetch simulator models", e);
    }
  }

  private String parseOsVersion(String version) {
    // com.apple.CoreSimulator.SimRuntime.iOS-15-0
    String versionWithHyphens = version.substring(version.lastIndexOf('.') + 1);  // -> iOS-15-0
    String versionWithDots = versionWithHyphens.replaceAll("-", ".");             // -> iOS.15.0
    return versionWithDots.substring(versionWithDots.indexOf('.') + 1);           // -> 15.0
  }

}
