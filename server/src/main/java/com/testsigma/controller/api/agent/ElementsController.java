/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.controller.api.agent;

import com.testsigma.exception.TestsigmaException;
import com.testsigma.service.ElementService;
import com.testsigma.web.request.ElementRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController(value = "agentElementsController")
@RequestMapping(path = "/api/agents/elements")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ElementsController {
  private final ElementService elementService;

  @RequestMapping(path = "/{versionId}/{name}", method = RequestMethod.PUT)
  public ResponseEntity<String> update(@PathVariable(value = "name") String name,
                                       @RequestBody ElementRequest elementRequest
  ) throws TestsigmaException, SQLException {
    elementService.updateByName(name, elementRequest);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
