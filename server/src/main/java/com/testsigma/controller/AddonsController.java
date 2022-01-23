package com.testsigma.controller;

import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.AddonMapper;
import com.testsigma.model.Addon;
import com.testsigma.service.AddonService;
import com.testsigma.web.request.AddonRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController(value = "addonsController")
@RequestMapping(path = "/addons")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AddonsController {

  private final AddonService service;
  private final AddonMapper mapper;

  @PostMapping
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void create(@RequestBody AddonRequest request) {
    log.debug("POST /addons with ::" + request);
    Addon plugin = mapper.mapRequest(request);
    plugin.setModifiedHash(ThreadContext.get("X-Request-Id"));
    service.create(plugin);
  }

  @DeleteMapping("/{externalUniqueId}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void delete(@PathVariable("externalUniqueId") String externalUniqueId) throws ResourceNotFoundException {
    log.debug("DELETE /addons/" + externalUniqueId);
    Addon plugin = service.findByExternalUniqueId(externalUniqueId);
    service.delete(plugin);
  }
}
