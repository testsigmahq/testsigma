/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.agent.ws.adapter;

import lombok.extern.log4j.Log4j2;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
public class MobileFrameSocketAdapter extends WebSocketAdapter {
  private static final Map<String, Session> allSessions = new ConcurrentHashMap<>();
  private String currentUUID;
  private boolean isDistributor = false;

  @Override
  public void onWebSocketConnect(Session session) {
    this.currentUUID = UUID.randomUUID().toString();
    log.info("Received a new session connection - " + currentUUID);
    log.info("Parameters of new session request - " + session.getUpgradeRequest().getQueryString());
    super.onWebSocketConnect(session);
    session.getPolicy().setMaxBinaryMessageBufferSize(1024 * 1024 * 1024);
    session.getPolicy().setIdleTimeout(3600000);
    session.getPolicy().setMaxTextMessageSize(1024 * 1024 * 1024);
    allSessions.put(this.currentUUID, session);
    setDistributorFlag(session);
  }

  public void onWebSocketText(String paramString) {
    super.onWebSocketText(paramString);
    try {
      if (isDistributor) {
        allSessions.forEach((uuid, otherSession) -> {
          if (canSendMessage(otherSession, uuid)) {
            try {
              allSessions.get(uuid).getRemote().sendString(paramString);
            } catch (IOException e) {
              log.error(e.getMessage(), e);
            }
          }
        });
      }
    } catch (IllegalStateException e) {
      log.error(e.getMessage(), e);
    }
  }

  public void onWebSocketClose(int statusCode, String reason) {
    log.info("onWebSocketClose for uuid - " + this.currentUUID);
    log.info("statusCode - " + statusCode + " , reason - " + reason);
    try {
      if (isDistributor) {
        allSessions.forEach((uuid, otherSession) -> {
          if (canCloseSession(otherSession, uuid)) {
            otherSession.close();
            allSessions.remove(uuid);
          }
        });
      }
      allSessions.remove(this.currentUUID);
      this.currentUUID = null;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  private boolean canSendMessage(Session otherSession, String uuid) {
    return !uuid.equalsIgnoreCase(this.currentUUID) && (otherSession != null) && (otherSession.isOpen());
  }

  private boolean canCloseSession(Session otherSession, String uuid) {
    return !uuid.equalsIgnoreCase(this.currentUUID) && (otherSession != null) && (otherSession.isOpen());
  }

  private void setDistributorFlag(Session session) {
    Map<String, List<String>> parameters = session.getUpgradeRequest().getParameterMap();
    if (parameters != null && parameters.size() > 0) {
      List<String> isDistributorFlag = parameters.get("isDistributor");
      if ((isDistributorFlag != null) && (isDistributorFlag.size() > 0)) {
        isDistributor = Boolean.parseBoolean(isDistributorFlag.get(0));
      }
    }
  }
}
