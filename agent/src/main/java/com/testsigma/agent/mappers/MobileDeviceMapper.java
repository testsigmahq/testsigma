/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.mappers;

import com.testsigma.agent.dto.AgentDeviceDTO;
import com.testsigma.agent.mobile.MobileDevice;
import com.android.ddmlib.IDevice;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface MobileDeviceMapper {

  @Mapping(target = "uniqueId", source = "serialNumber")
  @Mapping(target = "apiLevel", expression = "java(device.getProperty(\"ro.build.version.sdk\"))")
  @Mapping(target = "osVersion", expression = "java(device.getProperty(\"ro.build.version.release\"))")
  @Mapping(target = "productModel", expression = "java(device.getProperty(\"ro.product.model\"))")
  @Mapping(target = "abi", expression = "java(device.getProperty(\"ro.product.cpu.abi\"))")
  @Mapping(target = "isOnline", expression = "java(device.isOnline())")
  @Mapping(target = "isEmulator", expression = "java(device.isEmulator())")
  MobileDevice map(IDevice device);

  AgentDeviceDTO map(MobileDevice mobileDevice);

  void merge(MobileDevice mobileDevice, @MappingTarget AgentDeviceDTO agentDeviceDTO);
}
