/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.service;

import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.model.ElementFilter;
import com.testsigma.repository.ElementFilterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ElementFilterService {

  private final ElementFilterRepository elementFilterRepository;

  public Page<ElementFilter> findAllVisible(Long versionId, Pageable pageable) {
    return elementFilterRepository.findAll(versionId, pageable);
  }

  public ElementFilter find(Long id) throws ResourceNotFoundException {
    return elementFilterRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("ElementFilter missing with" + id));
  }

  public ElementFilter create(ElementFilter elementFilter) {
    return elementFilterRepository.save(elementFilter);
  }

  public ElementFilter update(ElementFilter elementFilter) {
    return elementFilterRepository.save(elementFilter);
  }

  public void destroy(Long id) throws ResourceNotFoundException {
    ElementFilter elementFilter = find(id);
    elementFilterRepository.delete(elementFilter);
  }

  public ElementFilterService normalizeFilter(ElementFilter elementFilter) {
    String queryHashToNormalize = decodeString(elementFilter.getQueryHash());
    elementFilter.setQueryHash(queryHashToNormalize.replace("\"false\"", "false").replace("\"true\"", "true"));
    return this;
  }

  private String decodeString(String encodedString) {
    String decodedString = "";
    decodedString = URLDecoder.decode(URLDecoder.decode(encodedString, StandardCharsets.UTF_8), StandardCharsets.UTF_8);
    log.info("Decoded the string twice to normal form!");
    return decodedString;
  }

}

