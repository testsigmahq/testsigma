package com.testsigma.controller;

import com.testsigma.dto.ServerDTO;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.mapper.ServerMapper;
import com.testsigma.model.Server;
import com.testsigma.service.ServerService;
import com.testsigma.web.request.ServerRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/servers")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ServersController {

  private final ServerService serverService;
  private final ServerMapper serverMapper;

  @GetMapping
  public ServerDTO show() throws TestsigmaException {
    Server server = serverService.findOne();
    return serverMapper.map(server);
  }

  @PutMapping()
  @ResponseStatus(HttpStatus.ACCEPTED)
  public ServerDTO update(@RequestBody ServerRequest request) throws TestsigmaException {
    Server server = serverService.findOne();
    serverMapper.merge(request, server);
    serverService.update(server);
    return serverMapper.map(server);
  }
}
