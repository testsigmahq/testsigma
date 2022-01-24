package com.testsigma.mapper;

import com.testsigma.dto.export.TestDeviceXMLDTO;
import com.testsigma.model.TestDevice;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ExportTestDeviceMapper {
  List<TestDeviceXMLDTO> mapEnvironments(List<TestDevice> environments);
}
