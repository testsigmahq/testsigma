/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.mapper;

import com.testsigma.dto.AgentDTO;
import com.testsigma.dto.export.AgentCloudXMLDTO;
import com.testsigma.dto.export.AgentXMLDTO;
import com.testsigma.model.Agent;
import com.testsigma.model.AgentBrowser;
import com.testsigma.web.request.AgentBrowserRequest;
import com.testsigma.web.request.AgentRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface AgentMapper {
  List<AgentXMLDTO> mapAgents(List<Agent> applications);

  @Mapping(target = "browserList", expression = "java(agentRequest.getAgentBrowserList())")
  Agent map(AgentRequest agentRequest);

  @Mapping(target = "browserList", expression = "java(agentRequest.getAgentBrowserList())")
  void map(AgentRequest agentRequest, @MappingTarget Agent agent);

  AgentBrowser map(AgentBrowserRequest agentBrowserRequest);

  @Named("mapAgent")
  @Mapping(target = "browserList", expression = "java(agent.getBrowserListDTO())")
  @Mapping(target = "jwtApiKey", ignore = true)
  AgentDTO map(Agent agent);

  @IterableMapping(qualifiedByName = "mapAgent")
  List<AgentDTO> map(List<Agent> agents);

    Agent copy(Agent testCase);

  List<Agent> mapCloudXMLList(List<AgentCloudXMLDTO> readValue);

  List<Agent> mapXMLList(List<AgentXMLDTO> readValue);
}
