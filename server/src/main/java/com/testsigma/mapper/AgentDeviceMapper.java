package com.testsigma.mapper;

import com.testsigma.dto.AgentDeviceDTO;
import com.testsigma.model.AgentDevice;
import com.testsigma.web.request.AgentDeviceRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface AgentDeviceMapper {
  @Mapping(target = "browserList", expression = "java(agentDeviceRequest.getAgentBrowserList())")
  void map(AgentDeviceRequest agentDeviceRequest, @MappingTarget AgentDevice agentDevice);

  @Mapping(target = "browserList", expression = "java(agentDeviceRequest.getAgentBrowserList())")
  AgentDevice map(AgentDeviceRequest agentDeviceRequest);

  AgentDeviceDTO map(AgentDevice agentDevice);

  List<AgentDeviceDTO> map(List<AgentDevice> agentDevices);

}
