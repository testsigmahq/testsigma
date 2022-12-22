/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.controller;

import com.testsigma.dto.EntityExternalMappingDTO;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.mapper.EntityExternalMappingMapper;
import com.testsigma.model.*;
import com.testsigma.service.EntityExternalMappingService;
import com.testsigma.service.IntegrationsService;
import com.testsigma.service.XrayCloudService;
import com.testsigma.specification.EntityExternalMappingsBuilder;
import com.testsigma.web.request.EntityExternalMappingRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.HttpResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/external_mappings")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EntityExternalMappingsController {
    private final EntityExternalMappingService externalMappingService;
    private final EntityExternalMappingMapper mapper;
    private final IntegrationsService externalConfigService;
    private final XrayCloudService xrayCloudService;

    @GetMapping
    public Page<EntityExternalMappingDTO> index(EntityExternalMappingsBuilder builder, Pageable pageable){
        Specification<EntityExternalMapping> specification = builder.build();
        Page<EntityExternalMapping> entityExternalMappings = externalMappingService.findAll(specification, pageable);
        List<EntityExternalMappingDTO> entityExternalMappingDTOS = mapper.mapToDTO(entityExternalMappings.getContent());
        return new PageImpl<>(entityExternalMappingDTOS, pageable, entityExternalMappings.getTotalElements());
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void destroy(@PathVariable("id") Long id) throws IOException, TestsigmaException {
        EntityExternalMapping mapping = this.externalMappingService.find(id);
        externalMappingService.destroy(mapping);
    }

    @GetMapping(path = "/{id}")
    public EntityExternalMappingDTO show(@PathVariable("id") Long id) throws IOException, TestsigmaException {
        EntityExternalMapping mapping = this.externalMappingService.fetch(id);
        return mapper.mapToDTO(mapping);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityExternalMappingDTO create(@RequestBody EntityExternalMappingRequest request) throws IOException, URISyntaxException, TestsigmaException {
        EntityExternalMapping mapping = mapper.map(request);
        mapping = externalMappingService.create(mapping);
        return mapper.mapToDTO(mapping);
    }

    @RequestMapping(path = "/xray_check_links", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void checkLinking(@RequestParam(value = "ids[]") Long[] ids, @RequestParam(value = "entityType") EntityType entityType) throws Exception {
        Optional<Integrations> externalApplicationConfig = this.externalConfigService.findOptionalByApplication(Integration.XrayCloud);
        List<EntityExternalMapping> entityExternalMappings = this.externalMappingService.findByEntityIds(ids, entityType, externalApplicationConfig.get().getId());
        if(entityExternalMappings.size() != ids.length) {
            throw new HttpResponseException(HttpStatus.FAILED_DEPENDENCY.value(), "Found Unlinked Entities");
        }
    }

    @PostMapping(path = "/push_to_xray")
    @ResponseStatus(HttpStatus.CREATED)
    public EntityExternalMappingDTO pushToXray(@RequestBody EntityExternalMappingRequest request) {
        EntityExternalMapping mapping = mapper.map(request);
        try {
            mapping.setPushFailed(null);
            mapping.setAssetsPushFailed(null);
            this.externalMappingService.save(mapping);
            xrayCloudService.pushBySuiteResultId(mapping.getEntityId());
        } catch (Exception e) {
            log.info("Failed to Start Xray Results Job for Suite Result Id : " + mapping.getEntityId());
            log.error(e.getMessage(), e);
        }
        return mapper.mapToDTO(mapping);
    }
}
