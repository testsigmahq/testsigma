package com.testsigma.service;

import com.testsigma.model.AgentDevice;
import com.testsigma.model.ProvisioningProfile;
import com.testsigma.model.ProvisioningProfileDevice;
import com.testsigma.repository.ProvisioningProfileDeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class ProvisioningProfileDeviceService {
  private final ProvisioningProfileDeviceRepository profileDeviceRepository;
  private final AgentDeviceService agentDeviceService;

  public List<ProvisioningProfileDevice> findAllByDeviceUDIdIn(List<String> deviceUDIDs) {
    return this.profileDeviceRepository.findAllByDeviceUDIdIn(deviceUDIDs);
  }

  public List<ProvisioningProfileDevice> findAllByProvisioningProfileId(Long provisioningProfileId) {
    return this.profileDeviceRepository.findAllByProvisioningProfileId(provisioningProfileId);
  }

  public List<ProvisioningProfileDevice> findAllByDeviceUDIdInAndProvisioningProfileIdNot(List<String> deviceUDIDs, Long profileId) {
    return this.profileDeviceRepository.findAllByDeviceUDIdInAndProvisioningProfileIdNot(deviceUDIDs, profileId);
  }

  public void deleteAllByDeviceUDIDIn(List<String> existingDevices) {
    List<ProvisioningProfileDevice> existing = findAllByDeviceUDIdIn(existingDevices);
    this.profileDeviceRepository.deleteAll(existing);
  }

  public ProvisioningProfileDevice findByAgentDeviceId(Long deviceId) {
    return this.profileDeviceRepository.findFirstByAgentDeviceId(deviceId);
  }

  public List<ProvisioningProfileDevice> create(List<String> deviceUDIDs, ProvisioningProfile provisioningProfile) {
    List<ProvisioningProfileDevice> devices = new ArrayList<>();
    log.info(String.format("Creating a provisioning profile device entries for devices UDID's [%s] and provisioning " +
      "profile id [%s]", deviceUDIDs, provisioningProfile.getId()));
    for (String deviceUDID : deviceUDIDs) {
      log.info("Creating provisioning profile device for UUID - " + deviceUDID);
      List<AgentDevice> agentDevices = new ArrayList<>();
      try {
        agentDevices = agentDeviceService.findByUniqueId(deviceUDID);
      } catch (Exception e) {
        log.info(String.format("Could not find device with UDID [%s] in agent devices. We will create an entry in " +
          "provisioned profile devices without associating with agent devices.", deviceUDID));
      }
      if (agentDevices.size() > 0) {
        log.info("Creating a provisioning profile device entry with agent device id");
        for (AgentDevice agentDevice : agentDevices) {
          devices.add(createEntryWithAgentDevice(deviceUDID, agentDevice, provisioningProfile));
        }
      } else {
        log.info("Creating a provisioning profile device entry with agent device id as NULL");
        devices.add(createEntryWithOutAgentDevice(deviceUDID, provisioningProfile));
      }
    }
    return devices;
  }

  public void updateAgentDevice(AgentDevice device) {
    log.info("Checking if device needs to be associated to any provisioning profile");
    List<ProvisioningProfileDevice> profileDevices = this.profileDeviceRepository.findByDeviceUDId(device.getUniqueId());
    if (profileDevices.size() > 0) {
      boolean entryAdded = false;
      Long provisioningProfileId = null;
      for (ProvisioningProfileDevice profileDevice : profileDevices) {
        provisioningProfileId = profileDevice.getProvisioningProfileId();
        if (profileDevice.getAgentDeviceId() == null) {
          log.info("Found an entry in provisioning profile devices with null agent device id. Updating that entry" +
            "with passed agent device id - " + device.getId());
          profileDevice.setAgentDeviceId(device.getId());
          this.profileDeviceRepository.save(profileDevice);
          entryAdded = true;
        }
      }
      if (!entryAdded && (provisioningProfileId != null)) {
        log.info("Entry was not added, but existing provisioning profile entries found...adding an entry now...");
        ProvisioningProfileDevice profileDevice = new ProvisioningProfileDevice();
        profileDevice.setAgentDeviceId(device.getId());
        profileDevice.setProvisioningProfileId(provisioningProfileId);
        profileDevice.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        profileDevice.setDeviceUDId(device.getUniqueId());
        this.profileDeviceRepository.save(profileDevice);
      } else {
        log.info("provisioning profile device entryAdded - " + entryAdded);
        log.info("uploaded provisioning profile id - " + provisioningProfileId);
      }
    } else {
      log.info("The connected device doesn't have any associated provisioning profile uploaded....");
    }
  }

  private ProvisioningProfileDevice createEntryWithAgentDevice(String deviceUDID, AgentDevice agentDevice,
                                                               ProvisioningProfile profile) {
    ProvisioningProfileDevice device = profileDeviceRepository.findByDeviceUDIdAndAgentDeviceIdAndProvisioningProfileId(
      deviceUDID, agentDevice.getId(), profile.getId()
    );
    if (device == null) {
      device = create(agentDevice, deviceUDID, profile);
      log.info("Created ProvisioningProfileDevice - " + device);
    }
    {
      log.info(String.format("A entry with device UDID - [%s] and agent Device ID - [%s] and provisioning " +
          "profile ID [%s] already exists, so skipping the entry creation.", deviceUDID, agentDevice.getId(),
        profile.getId()));
    }
    return device;
  }

  private ProvisioningProfileDevice createEntryWithOutAgentDevice(String deviceUDID, ProvisioningProfile profile) {
    ProvisioningProfileDevice device = profileDeviceRepository.findByDeviceUDIdAndProvisioningProfileIdAndAgentDeviceIdIsNull(
      deviceUDID, profile.getId()
    );
    if (device == null) {
      device = create(null, deviceUDID, profile);
      log.info("Created ProvisioningProfileDevice - " + device);
    }
    {
      log.info("A entry with empty agent device id already exists. Skipping the default entry creation with " +
        "null agent device id");
    }
    return device;
  }

  private ProvisioningProfileDevice create(AgentDevice agentDevice, String deviceUDID, ProvisioningProfile provisioningProfile) {
    ProvisioningProfileDevice device = new ProvisioningProfileDevice();
    device.setCreatedDate(new Timestamp(System.currentTimeMillis()));
    device.setDeviceUDId(deviceUDID);
    device.setProvisioningProfileId(provisioningProfile.getId());
    if (agentDevice != null)
      device.setAgentDeviceId(agentDevice.getId());
    return profileDeviceRepository.save(device);
  }
}
