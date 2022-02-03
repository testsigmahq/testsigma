package com.testsigma.agent.services;

import com.testsigma.agent.browsers.AgentBrowser;
import com.testsigma.agent.browsers.LinuxBrowsers;
import com.testsigma.agent.browsers.MacBrowsers;
import com.testsigma.agent.browsers.WindowsBrowsers;
import com.testsigma.agent.config.AgentConfig;
import com.testsigma.agent.constants.AgentOs;
import com.testsigma.agent.dto.AgentDTO;
import com.testsigma.agent.http.ServerURLBuilder;
import com.testsigma.agent.http.WebAppHttpClient;
import com.testsigma.agent.utils.NetworkUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.testsigma.automator.exceptions.AgentDeletedException;
import com.testsigma.automator.http.HttpResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
public class AgentBrowserService {

  private final AgentConfig agentConfig;
  private final WebAppHttpClient httpClient;
  private final MacBrowsers macBrowsers;
  private final LinuxBrowsers linuxBrowsers;
  private final WindowsBrowsers windowsBrowsers;

  @Getter
  private ArrayList<AgentBrowser> browserList;

  @PostConstruct
  public void initialise() {
    try {
      if (SystemUtils.IS_OS_MAC) {
        log.debug("initializing browsers list for mac");
        this.browserList = macBrowsers.getBrowserList();
      } else if (SystemUtils.IS_OS_LINUX) {
        log.debug("initializing browsers list for linux");
        this.browserList = linuxBrowsers.getBrowserList();
      } else if (SystemUtils.IS_OS_WINDOWS) {
        log.debug("initializing browsers list for windows");
        this.browserList = windowsBrowsers.getBrowserList();
      }
    } catch (Exception e) {
      log.info("Error while collecting browser list from agent system....");
      log.error(e.getMessage(), e);
    }
  }

  public void sync() throws AgentDeletedException {
    try {
      if (!startSync()) {
        return;
      }

      log.info("Syncing agent details");
      String hostName = AgentService.getComputerName();
      String uuid = agentConfig.getUUID();
      AgentDTO agentDTO = new AgentDTO();
      AgentOs osType = AgentOs.getLocalAgentOs();
      agentDTO.setOsType(osType);
      agentDTO.setOsVersion(AgentService.getOsVersion());
      agentDTO.setHostName(hostName);
      agentDTO.setIpAddress(NetworkUtil.getCurrentIpAddress());
      agentDTO.setAgentVersion(this.agentConfig.getAgentVersion());
      agentDTO.setBrowserList(this.getBrowserList());
      String authHeader = WebAppHttpClient.BEARER + " " + this.agentConfig.getJwtApiKey();
      HttpResponse<AgentDTO> response = httpClient.put(ServerURLBuilder.agentURL(uuid), agentDTO,
        new TypeReference<>() {
        }, authHeader);
      log.debug(response);
      if (response.getStatusCode() == HttpStatus.OK.value()) {
        log.info("Successfully updated latest agent details...");
      } else {
        log.info("Failed to sync latest hybrid agent details to application server");
        log.info("Error code: " + response.getStatusCode() + " - " + response.getStatusMessage());
      }
    } catch (AgentDeletedException e) {
      throw e;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    log.debug("Finished syncing agent details");
  }

  public boolean startSync() {
    boolean sync = true;
    if (agentConfig.getRegistered().equals(Boolean.FALSE)) {
      log.debug("Agent is not yet registered. Skipping browser sync");
      sync = false;
    }

    return sync;
  }
}
