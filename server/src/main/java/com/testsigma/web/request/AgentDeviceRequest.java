package com.testsigma.web.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.testsigma.model.AgentBrowser;
import com.testsigma.model.MobileOs;
import com.testsigma.service.ObjectMapperService;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Data
@Log4j2
public class AgentDeviceRequest {
  private String name;
  private String uniqueId;
  private String productModel;
  private String apiLevel;
  private String osVersion;
  private MobileOs osName;
  private String abi;
  private Boolean isEmulator;
  private Boolean isOnline;
  private Integer screenWidth;
  private Integer screenHeight;
  private List<AgentBrowserRequest> browserList;

  public List<AgentBrowser> getAgentBrowserList() {
    return browserList == null ? new ArrayList<>() :
      new ObjectMapperService().parseJson(new ObjectMapperService().convertToJson(browserList), new TypeReference<>() {
      });
  }
}
