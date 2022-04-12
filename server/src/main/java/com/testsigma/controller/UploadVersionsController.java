/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.controller;


import com.testsigma.dto.UploadVersionDTO;
import com.testsigma.exception.ResourceNotFoundException;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/upload_versions")
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

  @RequestMapping(path = "/{id}", method = RequestMethod.GET)
  public UploadVersionDTO show(@PathVariable("id") Long id) throws ResourceNotFoundException {
    return uploadMapper.mapVersion(uploadVersionService.find(id));
  }

  @DeleteMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void delete(@PathVariable("id") Long id) throws ResourceNotFoundException {
    uploadVersionService.delete(uploadVersionService.find(id));
  }


  @GetMapping(value = "/{id}/download")
  public void download(@PathVariable("id") Long id, HttpServletResponse response) throws ResourceNotFoundException, IOException {
    UploadVersion version = this.uploadVersionService.find(id);
    String preSignedURL = this.uploadVersionService.getPreSignedURL(version);
    response.sendRedirect(preSignedURL);
  }

}
