/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.controller;

import com.testsigma.dto.ProvisioningProfileDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.mapper.ProvisioningProfileMapper;
import com.testsigma.model.ProvisioningProfile;
import com.testsigma.model.ProvisioningProfileDevice;
import com.testsigma.service.CertificateService;
import com.testsigma.service.ProvisioningProfileDeviceService;
import com.testsigma.service.ProvisioningProfileService;
import com.testsigma.specification.ProvisioningProfilesBuilder;
import com.testsigma.web.request.ProvisioningProfileRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping(path = "/settings/provisioning_profiles")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProvisioningProfilesController {

  private final ProvisioningProfileService service;
  private final ProvisioningProfileMapper mapper;
  private final CertificateService certificateService;
  private final ProvisioningProfileDeviceService provisioningProfileDeviceService;

  @GetMapping
  public Page<ProvisioningProfileDTO> index(ProvisioningProfilesBuilder builder, Pageable pageable) {
    log.info("Get request /settings/provisioning_profiles");
    Specification<ProvisioningProfile> spec = builder.build();
    Page<ProvisioningProfile> profiles = this.service.findAll(spec, pageable);
    for (ProvisioningProfile profile : profiles) {
      certificateService.setPreSignedURLs(profile);
      setDeviceUuids(profile);
    }
    List<ProvisioningProfileDTO> dtos = mapper.map(profiles.getContent());
    return new PageImpl<>(dtos, pageable, profiles.getTotalElements());
  }

  @GetMapping(value = "/{id}")
  public ProvisioningProfileDTO show(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
    log.info("Get request /settings/provisioning_profiles/" + id);
    ProvisioningProfile profile = this.service.find(id);
    certificateService.setPreSignedURLs(profile);
    setDeviceUuids(profile);
    return this.mapper.map(profile);
  }

  @DeleteMapping(value = "/{id}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void destroy(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
    log.info("Delete request /settings/provisioning_profiles/" + id);
    this.service.destroy(id);
  }

  @PutMapping(value = "/{id}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public ProvisioningProfileDTO update(@PathVariable(value = "id") Long id,
                                       @ModelAttribute ProvisioningProfileRequest request)
    throws TestsigmaException {
    log.info("Put request /settings/provisioning_profiles/" + id + " data:" + request);
    ProvisioningProfile profile = this.service.find(id);
    this.mapper.merge(profile, request);
    profile.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
    profile = this.service.update(profile);
    setDeviceUuids(profile);
    return this.mapper.map(profile);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ProvisioningProfileDTO create(@RequestBody ProvisioningProfileRequest request) throws ResourceNotFoundException {
    log.info("Post request /settings/provisioning_profiles/ data:" + request);
    ProvisioningProfile profile = this.mapper.map(request);
    profile.setCreatedDate(new Timestamp(System.currentTimeMillis()));
    profile = this.service.create(profile);
    return this.mapper.map(profile);
  }

  private void setDeviceUuids(ProvisioningProfile provisioningProfile) {
    List<ProvisioningProfileDevice> profileDevices = provisioningProfileDeviceService
      .findAllByProvisioningProfileId(provisioningProfile.getId());
    List<String> deviceUuids = profileDevices.stream().map(ProvisioningProfileDevice::getDeviceUDId).distinct()
      .collect(Collectors.toList());
    provisioningProfile.setDeviceUDIDs(deviceUuids);
  }
}
