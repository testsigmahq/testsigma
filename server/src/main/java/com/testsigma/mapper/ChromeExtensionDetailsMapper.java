package com.testsigma.mapper;

import com.testsigma.dto.ChromeExtensionDetailsDTO;
import com.testsigma.model.ChromeExtensionDetails;
import com.testsigma.web.request.ChromeExtensionDetailsRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ChromeExtensionDetailsMapper {

  ChromeExtensionDetailsDTO map(ChromeExtensionDetails chromeExtensionDetails);

  ChromeExtensionDetails map(ChromeExtensionDetailsRequest chromeExtensionDetailsRequest);

  void merge(ChromeExtensionDetailsRequest chromeExtensionDetailsRequest, @MappingTarget ChromeExtensionDetails chromeExtensionDetails);
}
