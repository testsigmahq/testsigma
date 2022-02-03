package com.testsigma.mapper;

import com.testsigma.dto.AdhocRunConfigurationDTO;
import com.testsigma.model.AdhocRunConfiguration;
import com.testsigma.web.request.AdhocRunConfigurationRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface AdhocRunConfigurationsMapper {
  AdhocRunConfiguration map(AdhocRunConfigurationRequest adhocRunConfigurationRequest);

  void map(AdhocRunConfigurationRequest adhocRunConfigurationRequest,
           @MappingTarget AdhocRunConfiguration target);

  List<AdhocRunConfigurationDTO> map(List<AdhocRunConfiguration> configurations);

  //  @Mapping(target = "workspaceType", expression="java(adhocRunConfiguration.getWorkspaceType().getId())")
  AdhocRunConfigurationDTO map(AdhocRunConfiguration adhocRunConfiguration);
}
