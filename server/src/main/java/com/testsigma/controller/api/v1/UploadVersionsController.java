/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.controller.api.v1;


import com.testsigma.dto.UploadVersionDTO;
import com.testsigma.mapper.UploadMapper;
import com.testsigma.model.UploadVersion;
import com.testsigma.service.UploadVersionService;
import com.testsigma.specification.SearchCriteria;
import com.testsigma.specification.UploadVersionSpecificationsBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController(value = "apiUploadVersionsController")
@RequestMapping(path = "/api/v1/upload_versions")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class UploadVersionsController {

  private final UploadMapper uploadMapper;
  private final UploadVersionService uploadVersionService;

  @GetMapping
  public Page<UploadVersionDTO> index(UploadVersionSpecificationsBuilder builder, Pageable pageable) {
    Specification<UploadVersion> spec = builder.build();
    Page<UploadVersion> versions = uploadVersionService.findAll(spec, pageable);
    Long deviceId = null;
    for (SearchCriteria searchCriteria : builder.params)
      if (searchCriteria.getKey().equals("deviceId"))
        deviceId = Long.parseLong(searchCriteria.getValue().toString());
    List<UploadVersion> uploadList = uploadVersionService.setSignedFlag(versions.getContent(), deviceId);
    List<UploadVersionDTO> uploadDTOS = uploadMapper.mapVersions(uploadList);
    return new PageImpl<>(uploadDTOS, pageable, versions.getTotalElements());
  }
}
