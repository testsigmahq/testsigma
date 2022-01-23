package com.testsigma.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Integration {
  BugZilla(1, IntegrationAuthType.AccessKey),
  Jira(2, IntegrationAuthType.AccessKey),
  Slack(3, IntegrationAuthType.AccessKey),
  MS_Teams_Connector(4, IntegrationAuthType.None),
  Freshrelease(5, IntegrationAuthType.AccessKey),
  Azure(6, IntegrationAuthType.AccessKey),
  Mantis(7, IntegrationAuthType.AccessKey),
  Zepel(8, IntegrationAuthType.AccessKey),
  BackLog(9, IntegrationAuthType.AccessKey),
  Youtrack(10, IntegrationAuthType.AccessKey),
  GoogleChat(11, IntegrationAuthType.AccessKey),
  Trello(12, IntegrationAuthType.AccessKey),
  Linear(13, IntegrationAuthType.AccessKey),
  TestsigmaLab(14, IntegrationAuthType.AccessKey),
  ClickUp(15,  IntegrationAuthType.AccessKey),


  Others(10000, IntegrationAuthType.AccessKey);

  private final Integer id;
  private final IntegrationAuthType authType;


  public static Integration getIntegration(Long id) {

    for (Integration type : Integration.values()) {
      if (new Long(type.getId().longValue()).equals(id)) {
        return type;
      }
    }
    return null;
  }

  public Boolean isJira() {
    return this.equals(Integration.Jira);
  }

  public Boolean isFreshrelease() {
    return this.equals(Integration.Freshrelease);
  }

  public Boolean isMantis() {
    return this.equals(Integration.Mantis);
  }

  public Boolean isAzure() {
    return this.equals(Integration.Azure);
  }

  public Boolean isBackLog() {
    return this.equals(Integration.BackLog);
  }

  public Boolean isZepel() {
    return this.equals(Integration.Zepel);
  }

  public Boolean isYoutrack() {
    return this.equals(Integration.Youtrack);
  }

  public Boolean isBugZilla() {
    return this.equals(Integration.BugZilla);
  }

  public Boolean isTrello() {
    return this.equals(Integration.Trello);
  }

  public Boolean isLinear() {
    return this.equals(Integration.Linear);
  }

  public Boolean isClickUp(){ return this.equals(Integration.ClickUp); }

}
