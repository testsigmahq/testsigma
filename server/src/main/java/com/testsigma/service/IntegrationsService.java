/******************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 *****************************************************************************/
package com.testsigma.service;

import com.testsigma.exception.IntegrationNotFoundException;
import com.testsigma.exception.TestsigmaDatabaseException;
import com.testsigma.mapper.IntegrationsMapper;
import com.testsigma.model.Integrations;
import com.testsigma.model.Integration;
import com.testsigma.repository.IntegrationsRepository;
import com.testsigma.web.request.IntegrationsRequest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service(value = "integrationsService")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@NoArgsConstructor
public class IntegrationsService {

  IntegrationsRepository integrationsRepository;
  PrivateGridService privateGridService;
  IntegrationsMapper mapper;

  /*
   * Method to create a new external workspace config
   */
  public Integrations create(IntegrationsRequest externalApplicationConfigReq) {
    Integrations integrations = mapper.map(externalApplicationConfigReq);
    integrations = integrationsRepository.save(integrations);
    return integrations;
  }

  /*
   * Method to update existing external workspace config
   */
  public Integrations update(IntegrationsRequest externalApplicationConfigReq, Long id)
    throws IntegrationNotFoundException, TestsigmaDatabaseException {
    Integrations integrations = find(id);
    integrations.setMetadata(externalApplicationConfigReq.getMetadata());
    integrations.setUsername(externalApplicationConfigReq.getUsername());
    integrations.setPassword(externalApplicationConfigReq.getPassword());
    integrations.setToken(externalApplicationConfigReq.getToken());
    integrations = integrationsRepository.save(integrations);
    return integrations;
  }

  /*
   * Method to update existing external workspace config
   */
  public Integrations save(Integrations config) {
    return integrationsRepository.save(config);
  }

  /**
   * @return external workspace config
   */
  public Integrations find(Long id)
    throws IntegrationNotFoundException {
    return integrationsRepository.findById(id).orElseThrow(() -> new IntegrationNotFoundException("missing with id:" + id));
  }

  /**
   * @return
   */
  public void destroy(Long id) throws IntegrationNotFoundException {
    Optional<Integrations> config = integrationsRepository.findById(id);
    if (!config.isPresent()) {
      throw new IntegrationNotFoundException("EXTERNAL APPLICATION CONFIG NOT FOUND");
    }
    if (config.get().getWorkspace() == Integration.PrivateGrid)
          this.privateGridService.cleanTable();
    integrationsRepository.delete(config.get());
  }

  /**
   * @return integrations pages
   */
  public Page<Integrations> findAll(Specification<Integrations> specification, Pageable pageable) {
    return integrationsRepository.findAll(specification, pageable);
  }

  public Integrations findByApplication(Integration application)
    throws IntegrationNotFoundException {
    return this.findOptionalByApplication(application).orElseThrow(() -> new IntegrationNotFoundException(application.name() + " - Integration Not Enabled"));
  }

  public Optional<Integrations> findOptionalByApplication(Integration application) {
    return this.integrationsRepository.findByWorkspaceId(application.getId().longValue());
  }

}
