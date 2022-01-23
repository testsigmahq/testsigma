/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.service;

import com.testsigma.event.EnvironmentEvent;
import com.testsigma.event.EventType;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.model.Environment;
import com.testsigma.repository.EnvironmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EnvironmentService {

  private final EnvironmentRepository environmentRepository;
  private final ApplicationEventPublisher applicationEventPublisher;

  public Environment find(Long id) throws ResourceNotFoundException {
    return environmentRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Test Data Not Found with id: " + id));
  }

  public Environment update(Environment environment) {
    environment = environmentRepository.save(environment);
    publishEvent(environment, EventType.UPDATE);
    return environment;
  }

  public Page<Environment> findAll(Specification<Environment> spec, Pageable pageable) {
    return environmentRepository.findAll(spec, pageable);
  }

  public void destroy(Long id) throws ResourceNotFoundException {
    Environment environment = find(id);
    this.environmentRepository.delete(environment);
    publishEvent(environment, EventType.DELETE);
  }

  public Environment create(Environment environment) {
    environment = this.environmentRepository.save(environment);
    publishEvent(environment, EventType.CREATE);
    return environment;
  }

  public void bulkDestroy(Long[] ids) throws Exception {
    Boolean allIdsDeleted = true;
    Exception throwable = new Exception();
    for (Long id : ids) {
      try {
        destroy(id);
      } catch (Exception ex) {
        allIdsDeleted = false;
        throwable = ex;
      }
    }
    if (!allIdsDeleted) {
      throw throwable;
    }
  }

  public void publishEvent(Environment environment, EventType eventType) {
    EnvironmentEvent<Environment> event = createEvent(environment, eventType);
    log.info("Publishing event - " + event.toString());
    applicationEventPublisher.publishEvent(event);
  }

  public EnvironmentEvent<Environment> createEvent(Environment environment, EventType eventType) {
    EnvironmentEvent<Environment> event = new EnvironmentEvent<>();
    event.setEventData(environment);
    event.setEventType(eventType);
    return event;
  }
}
