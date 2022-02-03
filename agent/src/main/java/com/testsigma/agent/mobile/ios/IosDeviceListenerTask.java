package com.testsigma.agent.mobile.ios;

import com.dd.plist.NSDictionary;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Log4j2
public class IosDeviceListenerTask implements Runnable {
  private static final int MAX_ERROR_THRESHOLD = 1000;
  private final Map<String, Consumer<DeviceConnectionMessage>> consumers;
  private InputStream inputStream;
  private boolean running = false;

  public IosDeviceListenerTask() {
    consumers = Collections.synchronizedMap(new HashMap<>());
  }

  public void start(InputStream inputStream) {
    this.inputStream = inputStream;
    running = true;
  }


  public String register(Consumer<DeviceConnectionMessage> deviceConnectionListener) {
    String uid = UUID.randomUUID().toString();
    consumers.put(uid, deviceConnectionListener);
    return uid;
  }

  public void unregister(String uid) {
    consumers.remove(uid);
  }

  @Override
  public void run() {
    log.info("Starting device listener task");
    int noOfErrors = 0;
    while (running) {
      try {
        int size = UsbMuxSocket.getSize(inputStream);
        if (size > 0) {
          NSDictionary dico = UsbMuxSocket.getNsDictionary(inputStream, size);
          log.info("Device connection unparsed information - " + dico.toXMLPropertyList());
          UsbMuxSocket.ResultType messageTypeEnum = UsbMuxSocket.retrieveMsgType(dico);
          DeviceConnectionMessage deviceConnectionMessage = new DeviceConnectionMessage();
          switch (messageTypeEnum) {
            case Attached:
              deviceConnectionMessage.device = buildDevice(dico);
              deviceConnectionMessage.type = DeviceConnectionMessage.Type.Add;
              log.info("Device connected. Parsed data - " + deviceConnectionMessage);
              notify(deviceConnectionMessage);
              break;
            case Detached:
              Device deviceDetachMessage = new Device();
              deviceDetachMessage.deviceId = Integer.valueOf(dico.get("DeviceID").toString());
              deviceConnectionMessage.device = deviceDetachMessage;
              deviceConnectionMessage.type = DeviceConnectionMessage.Type.Remove;
              log.info("Device removed. Parsed data - " + deviceConnectionMessage);
              notify(deviceConnectionMessage);
          }
        }
        noOfErrors = 0;
      } catch (Exception e) {
        noOfErrors++;
        if (noOfErrors > MAX_ERROR_THRESHOLD) {
          log.error("Error count while listening to ios devices crossed 1000. Stopping the listener.");
          stop();
        }
        log.error(e.getMessage(), e);
      }
    }
  }

  private void notify(DeviceConnectionMessage deviceMsg) {
    consumers.values().forEach(c -> c.accept(deviceMsg));
  }

  private Device buildDevice(NSDictionary dico) {
    Device deviceAttachMessage = new Device();
    NSDictionary properties = (NSDictionary) dico.get("Properties");
    if (properties != null) {
      deviceAttachMessage.serialNumber = properties.get("SerialNumber").toString();
      deviceAttachMessage.connectionType = properties.get("ConnectionType").toString();
      deviceAttachMessage.deviceId = Integer.valueOf(properties.get("DeviceID").toString());
      deviceAttachMessage.locationId = properties.get("LocationID").toString();
      deviceAttachMessage.productId = properties.get("ProductID").toString();
    }
    return deviceAttachMessage;
  }

  public void stop() {
    log.info("Stopping device listener task");
    if (running) {
      try {
        inputStream.close();
      } catch (IOException e) {
        inputStream = null;
      }
    }
    running = false;
  }
}
