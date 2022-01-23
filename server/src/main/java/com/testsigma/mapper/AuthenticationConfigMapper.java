package com.testsigma.mapper;


import com.testsigma.config.AdditionalPropertiesConfig;
import com.testsigma.dto.AuthenticationConfigDTO;
import com.testsigma.web.request.AuthenticationConfigRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface AuthenticationConfigMapper {

  AuthenticationConfigDTO map(AdditionalPropertiesConfig config);

  void merge(AuthenticationConfigRequest authenticationConfigRequest, @MappingTarget AdditionalPropertiesConfig additionalPropertiesConfig);
}
