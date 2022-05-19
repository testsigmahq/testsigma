/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.controller;

import com.testsigma.dto.PrivateGridNodeDTO;
import com.testsigma.mapper.PrivateGridNodeMapper;
import com.testsigma.model.*;
import com.testsigma.service.PrivateGridService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/grid_nodes")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PrivateGridNodesController {
  private final PrivateGridService privateGridService;
  private final PrivateGridNodeMapper mapper;

  @RequestMapping(method = RequestMethod.GET)
  public List<PrivateGridNodeDTO> index() {
    List<PrivateGridNode> nodes = privateGridService.findAll();
    return mapper.mapList(nodes);
  }

}
