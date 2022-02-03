package com.testsigma.mapper;

import com.testsigma.dto.TestCaseResultExternalMappingDTO;
import com.testsigma.model.TestCaseResultExternalMapping;
import com.testsigma.web.request.TestCaseResultExternalMappingRequest;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface TestCaseResultExternalMappingMapper {

  TestCaseResultExternalMapping map(TestCaseResultExternalMappingRequest request);

  List<TestCaseResultExternalMappingDTO> mapToDTO(List<TestCaseResultExternalMapping> mappings);

  TestCaseResultExternalMappingDTO mapToDTO(TestCaseResultExternalMapping mapping);
}
