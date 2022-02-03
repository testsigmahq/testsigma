/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.agent.ws.server;

import com.testsigma.agent.config.AgentConfig;
import com.testsigma.agent.dto.AgentWebServerConfigDTO;
import com.testsigma.agent.services.AgentWebServerService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.KeyStore;

@Log4j2
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Data
public class AgentWebServer {
  private final AgentWebServerService agentWebServerService;
  private final AgentConfig agentConfig;

  @Value("${server.port}")
  private String defaultHttpPort;
  @Value("${agent.default.https.port}")
  private String defaultHttpsPort;

  private byte[] certificate;
  private byte[] key;
  private boolean isCertificateFetched = false;
  private Server server;
  private ServerConnector defaultHttpsServerConnector;
  private ServerConnector customHttpServerConnector;
  private ServerConnector customHttpsServerConnector;

  public void startWebServerConnectors() {
    try {
      startDefaultConnectors();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      Runtime.getRuntime().exit(-1);
    }
  }

  @PreDestroy
  public void stopWebServerConnectors() {
    try {
      stopDefaultConnectors();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  private void startDefaultConnectors() throws Exception {
    fetchWebServerCertificate();
    startDefaultHttpConnector();
    startDefaultHttpsConnector();
  }

  private void stopDefaultConnectors() throws Exception {
    stopDefaultHttpConnector();
    stopDefaultHttpsConnector();
  }

  private void fetchWebServerCertificate() throws Exception {
    if (!this.isCertificateFetched) {
      AgentWebServerConfigDTO agentWebServerConfigDTO = agentWebServerService.getWebServerCertificate();
      if (agentWebServerConfigDTO == null) {
        throw new Exception("Could not fetch agent web server config from Testsigma cloud...");
      }

      this.certificate = getCertificate(agentWebServerConfigDTO);
      this.key = agentWebServerConfigDTO.getKey();
      this.isCertificateFetched = true;
    }

  }

  private void startDefaultHttpConnector() {
    log.info("Starting agent HTTP connector at port - " + this.defaultHttpPort);
  }

  private void startDefaultHttpsConnector() throws Exception {
    this.defaultHttpsServerConnector = this.startHttpsConnector(Integer.parseInt(this.defaultHttpsPort));
  }

  private void stopDefaultHttpConnector() {
    log.info("Stopping agent HTTP connector running on port - " + this.defaultHttpPort);
  }

  private void stopDefaultHttpsConnector() throws Exception {
    if ((this.defaultHttpsServerConnector != null) && (this.defaultHttpsServerConnector.isRunning())) {
      log.info("Stopping agent HTTP connector running on port - " + this.defaultHttpsPort);
      this.defaultHttpsServerConnector.stop();
    }
    this.defaultHttpsServerConnector = null;
  }

  private ServerConnector startHttpsConnector(int httpsPort) throws Exception {
    log.info("Starting agent HTTPS connector at port - " + httpsPort);
    ServerConnector serverConnector;
    SslContextFactory.Server context = new SslContextFactory.Server();
    KeyStore keyStore = KeyStore.getInstance("PKCS12");
    try (ByteArrayInputStream certificateStream = new ByteArrayInputStream(this.certificate)) {
      keyStore.load(certificateStream, new String(this.key, StandardCharsets.UTF_8).toCharArray());
    }
    context.setKeyStore(keyStore);
    context.setKeyStorePassword(new String(this.key, StandardCharsets.UTF_8));

    serverConnector = new ServerConnector(this.server, context);
    serverConnector.setPort(httpsPort);
    serverConnector.start();
    this.server.addConnector(serverConnector);
    return serverConnector;
  }

  private byte[] getCertificate(AgentWebServerConfigDTO agentWebServerConfigDTO) {
    try {
      if (agentWebServerConfigDTO.getCertificatePresignedURL() != null) {
        String fileName = FilenameUtils.getName(agentWebServerConfigDTO.getCertificatePresignedURL().getPath());
        File destinationFile = Paths.get(FileUtils.getTempDirectory().toString(), fileName).toFile();
        FileUtils.copyURLToFile(agentWebServerConfigDTO.getCertificatePresignedURL(), destinationFile, (60 * 1000), (60 * 1000));
        if (destinationFile.exists()) {
          return FileUtils.readFileToByteArray(destinationFile);
        } else {
          log.error("Couldn't not fetch certificate from - " + agentWebServerConfigDTO.getCertificatePresignedURL());
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return agentWebServerConfigDTO.getCertificate();
  }
}
