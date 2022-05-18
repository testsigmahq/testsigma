/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.controller;

import com.testsigma.exception.TestsigmaException;
import com.testsigma.model.*;
import com.testsigma.service.PlatformsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/platforms")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PlatformsController {
  private final PlatformsService platformService;

  @RequestMapping(method = RequestMethod.GET)
  public List<Platform> platforms(@RequestParam WorkspaceType workspaceType,
                                  @RequestParam TestPlanLabType testPlanLabType) throws TestsigmaException {
    return platformService.getSupportedPlatforms(workspaceType,
        testPlanLabType);
  }

  @RequestMapping(path = "/{platform}/os_versions", method = RequestMethod.GET)
  public List<PlatformOsVersion> osVersion(@PathVariable Platform platform,
                                           @RequestParam WorkspaceType workspaceType,
                                           @RequestParam TestPlanLabType testPlanLabType) throws TestsigmaException {
    return platformService.getPlatformOsVersions(platform, workspaceType,
        testPlanLabType);
  }

  @RequestMapping(path = "/{platformOsVersionId}/os_version", method = RequestMethod.GET)
  public PlatformOsVersion osVersion(@PathVariable Long platformOsVersionId,
                                     @RequestParam TestPlanLabType testPlanLabType) throws TestsigmaException {
    return platformService.getPlatformOsVersion(platformOsVersionId, testPlanLabType);
  }

  @RequestMapping(path = "/{platform}/{osVersion}/browsers", method = RequestMethod.GET)
  public List<Browsers> browsers(@PathVariable Platform platform,
                                        @PathVariable String osVersion,
                                        @RequestParam TestPlanLabType testPlanLabType) throws TestsigmaException {
    return platformService.getPlatformSupportedBrowsers(platform, osVersion, testPlanLabType);
  }

  @RequestMapping(path = "/{platform}/{osVersion}/browser/{browserName}/versions", method = RequestMethod.GET)
  public List<PlatformBrowserVersion> browserVersions(@PathVariable Platform platform,
                                                      @PathVariable String osVersion,
                                                      @PathVariable Browsers browserName,
                                                      @RequestParam TestPlanLabType testPlanLabType) throws TestsigmaException {
    return platformService.getPlatformBrowsers(platform, osVersion,
      browserName, testPlanLabType);
  }

  @RequestMapping(path = "/{platformBrowserVersionId}/browser_version", method = RequestMethod.GET)
  public PlatformBrowserVersion browserVersion(@PathVariable Long platformBrowserVersionId,
                                               @RequestParam TestPlanLabType testPlanLabType) throws TestsigmaException {
    return platformService.getPlatformBrowserVersion(platformBrowserVersionId, testPlanLabType);
  }


  @RequestMapping(path = "/{platform}/{osVersion}/screen_resolutions", method = RequestMethod.GET)
  public List<PlatformScreenResolution> screenResolutions(@PathVariable Platform platform,
                                                          @PathVariable String osVersion,
                                                          @RequestParam TestPlanLabType testPlanLabType) throws TestsigmaException {
    return platformService.getPlatformScreenResolutions(platform, osVersion,
        testPlanLabType);
  }

  @RequestMapping(path = "/{platformScreenResolutionId}/screen_resolution", method = RequestMethod.GET)
  public PlatformScreenResolution screenResolution(@PathVariable Long platformScreenResolutionId,
                                                   @RequestParam TestPlanLabType testPlanLabType) throws TestsigmaException {
    return platformService.getPlatformScreenResolution(platformScreenResolutionId, testPlanLabType);
  }

  @RequestMapping(path = "/{platform}/devices", method = RequestMethod.GET)
  public List<PlatformDevice> mobileDevices(@PathVariable Platform platform, @RequestParam List<String> osVersions,
                                            @RequestParam TestPlanLabType testPlanLabType)
    throws TestsigmaException {
    return platformService.getPlatformDevices(platform, osVersions, testPlanLabType);
  }

  @RequestMapping(path = "/{platform}/{osVersion}/devices", method = RequestMethod.GET)
  public List<PlatformDevice> osMobileDevices(@PathVariable Platform platform, @PathVariable String osVersion,
                                              @RequestParam WorkspaceType workspaceType,
                                              @RequestParam TestPlanLabType testPlanLabType)
    throws TestsigmaException {
    return platformService.getPlatformDevices(platform, osVersion, testPlanLabType);
  }

  @RequestMapping(path = "/{platformDeviceId}/device", method = RequestMethod.GET)
  public PlatformDevice platformDevice(@PathVariable Long platformDeviceId,
                                       @RequestParam TestPlanLabType testPlanLabType) throws TestsigmaException {
    return platformService.getPlatformDevice(platformDeviceId, testPlanLabType);
  }

}
