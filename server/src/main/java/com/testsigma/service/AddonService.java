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
import com.testsigma.mapper.AddonMapper;
import com.testsigma.model.*;
import com.testsigma.repository.AddonRepository;
import com.testsigma.repository.KibbutzPluginTestDataFunctionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired, @Lazy}))
@Log4j2
public class AddonService {
  private final AddonRepository repository;
  private final AddonNaturalTextActionService addonNaturalTextActionService;
  private final AddonMapper mapper;
  private final TestStepService testStepService;
  private final KibbutzPluginTestDataFunctionService testDataFunctionService;

  public void create(Addon plugin) {
    Addon pluginDB = fetchPlugin(plugin);
    if (pluginDB.getStatus() != AddonStatus.UNINSTALLED ) {
      pluginDB = repository.save(pluginDB);
      List<AddonNaturalTextAction> nlpList = plugin.getActions();
      saveNLP(pluginDB, nlpList);
      List<KibbutzPluginTestDataFunction> testDataFunctionList = plugin.getTestDataFunctions();
      saveTestDataFunctions(pluginDB, testDataFunctionList);
      cleanupStaleEntriesPostInstall(plugin, pluginDB);
    } else {
      pluginDB.setStatus(AddonStatus.INSTALLED);
      repository.save(pluginDB);
    }
  }

  private void saveTestDataFunctions(Addon pluginDB, List<KibbutzPluginTestDataFunction> testDataFunctionList) {
    for (KibbutzPluginTestDataFunction testDataFunction : testDataFunctionList) {
      testDataFunction.setAddonId(pluginDB.getId());
      testDataFunctionService.create(testDataFunction);
    }
  }

  private void saveNLP(Addon pluginDB, List<AddonNaturalTextAction> nlpList) {
    for (AddonNaturalTextAction nlp : nlpList) {
      nlp.setAddonId(pluginDB.getId());
      addonNaturalTextActionService.create(nlp);
    }
  }

  private void cleanupStaleEntriesPostInstall(Addon plugin, Addon pluginDB) {
    Optional<Addon> optionalAddon = repository.findTopByExternalUniqueIdAndStatus(plugin.getExternalUniqueId(), AddonStatus.DRAFT);
    if (optionalAddon.isPresent()) {
      if (optionalAddon.get().getExternalInstalledVersionUniqueId().equals(plugin.getExternalInstalledVersionUniqueId()) && !Objects.equals(optionalAddon.get().getId(), pluginDB.getId())) {
        repository.delete(optionalAddon.get());
      }
    }
  }

  private Addon fetchPlugin(Addon plugin) {
    Optional<Addon> optionalAddon;
    Addon dbPlugin;
    optionalAddon = repository.findTopByExternalUniqueIdAndStatus(plugin.getExternalUniqueId(), AddonStatus.UNINSTALLED);
    if (optionalAddon.isEmpty()) {
      if (plugin.getStatus().equals(AddonStatus.DRAFT))
        optionalAddon = repository.findTopByExternalUniqueIdAndStatus(plugin.getExternalUniqueId(), plugin.getStatus());
      else
        optionalAddon = repository.findTopByExternalUniqueIdAndStatus(plugin.getExternalUniqueId(), plugin.getStatus());
      if (optionalAddon.isPresent()) {
        dbPlugin = optionalAddon.get();
        mapper.merge(plugin, dbPlugin);
      } else
        dbPlugin = plugin;
      return dbPlugin;
    } else {
      return optionalAddon.get();
    }
  }

  public Addon findByExternalUniqueId(String externalUniqueId) throws ResourceNotFoundException {
    return repository.findByExternalUniqueIdAndStatus(externalUniqueId, AddonStatus.INSTALLED).orElseThrow(() -> new ResourceNotFoundException("No Plugin installed with ::" + externalUniqueId));
  }

  public Addon findById(Long addonId) throws ResourceNotFoundException {
    return repository.findById(addonId).orElseThrow(() -> new ResourceNotFoundException("No Plugin with id - " + addonId));
  }

  public void delete(Addon plugin) {
    List<AddonNaturalTextAction> actions = addonNaturalTextActionService.findAllByAddonId(plugin.getId());
    List<Long> actionIds = actions.stream().map(AddonNaturalTextAction::getId).collect(Collectors.toList());
    Integer usageCount = testStepService.countAllByAddonActionIdIn(actionIds);
    if (usageCount == 0)
      repository.delete(plugin);
    else {
      plugin.setStatus(AddonStatus.UNINSTALLED);
      repository.save(plugin);
    }
  }
}

