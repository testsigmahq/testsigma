package com.testsigma.mapper;

import com.testsigma.dto.AuthUserDTO;
import com.testsigma.model.AuthUser;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface AuthUserMapper {
  AuthUserDTO map(AuthUser authUser);
}
