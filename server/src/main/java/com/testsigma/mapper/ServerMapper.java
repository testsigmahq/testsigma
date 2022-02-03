package com.testsigma.mapper;

import com.testsigma.dto.ServerDTO;
import com.testsigma.model.Server;
import com.testsigma.web.request.ServerRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ServerMapper {
  ServerDTO map(Server server);

  @Mapping(target = "serverUuid", ignore = true)
  @Mapping(target = "serverOs", ignore = true)
  void merge(ServerRequest serverRequest, @MappingTarget Server server);
}
