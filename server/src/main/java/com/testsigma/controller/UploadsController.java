/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.controller;


import com.testsigma.dto.UploadDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.mapper.UploadMapper;
import com.testsigma.model.Upload;
import com.testsigma.service.TestDeviceService;
import com.testsigma.service.UploadService;
import com.testsigma.specification.UploadSpecificationsBuilder;
import com.testsigma.web.request.UploadRequest;
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
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/uploads")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class UploadsController {

  private final UploadService uploadService;
  private final TestDeviceService testDeviceService;
  private final UploadMapper uploadMapper;

  @GetMapping
  public Page<UploadDTO> index(UploadSpecificationsBuilder builder, Pageable pageable) {
    Specification<Upload> spec = builder.build();
    Page<Upload> uploads = uploadService.findAll(spec, pageable);
    List<UploadDTO> uploadDTOS = uploadMapper.map(uploads.getContent());
    uploadDTOS = uploadService.setSignedFlag(uploadDTOS, builder);
    return new PageImpl<>(uploadDTOS, pageable, uploads.getTotalElements());
  }

  @PostMapping
  public UploadDTO create(@ModelAttribute @Valid UploadRequest uploadRequest)
    throws TestsigmaException {
    Upload upload = uploadService.create(uploadRequest);
    return uploadMapper.map(upload);
  }

  @PostMapping(path = "/{id}")
  public UploadDTO update(@PathVariable("id") Long id, @ModelAttribute UploadRequest uploadRequest)
    throws TestsigmaException {
    Upload upload = uploadService.find(id);
    upload = uploadService.update(upload, uploadRequest);
    return uploadMapper.map(upload);
  }

  @RequestMapping(path = "/{id}", method = RequestMethod.GET)
  public UploadDTO show(@PathVariable("id") Long id) throws ResourceNotFoundException {
    return uploadMapper.map(uploadService.find(id));
  }

  @DeleteMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void delete(@PathVariable("id") Long id) throws ResourceNotFoundException {
    this.testDeviceService.resentAppUploadIdToNull(id);
    uploadService.delete(uploadService.find(id));
  }

  @DeleteMapping(value = "/bulk")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void bulkDelete(@RequestParam(value = "ids[]") Long[] ids) throws ResourceNotFoundException {
    for (Long id : ids) {
      uploadService.delete(uploadService.find(id));
    }
  }

  @GetMapping(value = "/{id}/download")
  public void download(@PathVariable("id") Long id, HttpServletResponse response) throws ResourceNotFoundException, IOException {
    Upload upload = this.uploadService.find(id);
    String preSignedURL = this.uploadService.getPreSignedURL(upload);
    response.sendRedirect(preSignedURL);
  }

}
