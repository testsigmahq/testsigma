/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.agent.ws.client;

import lombok.extern.log4j.Log4j2;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.nio.ByteBuffer;
import java.util.concurrent.Future;

@Log4j2
@WebSocket(maxIdleTime = 3600000, maxTextMessageSize = 1024 * 1024 * 1024)
public class SimpleEchoSocket {
  private Session session;

  @OnWebSocketClose
  public void onClose(int statusCode, String reason) {
    log.info("Connection closed " + statusCode + " - " + reason);
    this.session = null;
  }

  @OnWebSocketError
  public void onError(Throwable throwable) {
    log.error(throwable.getMessage(), throwable);
  }

  @OnWebSocketConnect
  public void onConnect(Session session) {
    log.info("Got connect...");
    session.getPolicy().setMaxBinaryMessageBufferSize(100 * 1024 * 1024);
    session.getPolicy().setIdleTimeout(3600000);
    this.session = session;
  }

  @OnWebSocketMessage
  public void onMessage(byte[] msg, int offset, int length) {
    try {
      Future<Void> fut = session.getRemote().sendBytesByFuture(ByteBuffer.wrap(msg));
    } catch (Throwable t) {
      log.error(t.getMessage(), t);
    }
  }

  @OnWebSocketMessage
  public void onTextMessage(String message) {
  }
}
