/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.mapper;

import com.testsigma.dto.ReportsDTO;
import com.testsigma.dto.api.APIElementDTO;
import com.testsigma.dto.ElementDTO;
import com.testsigma.dto.ElementNotificationDTO;
import com.testsigma.dto.export.ElementCloudXMLDTO;
import com.testsigma.dto.export.ElementXMLDTO;
import com.testsigma.model.Element;
import com.testsigma.model.ElementMetaData;
import com.testsigma.model.ElementMetaDataRequest;
import com.testsigma.model.Report;
import com.testsigma.web.request.ElementRequest;
import com.testsigma.web.request.testproject.TestProjectElementRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ReportsMapper {
    @Mapping(target = "reportConfiguration.reportCriteriaMappings", ignore = true)
    ReportsDTO map(Report report);

    @Mapping(target = "reportConfiguration.reportCriteriaMappings", ignore = true)
    List<ReportsDTO> mapDTOs(List<Report> reports);
}
