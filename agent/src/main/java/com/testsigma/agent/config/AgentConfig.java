package com.testsigma.agent.config;

import com.testsigma.agent.exception.TestsigmaException;
import com.testsigma.agent.utils.PathUtil;
import lombok.Data;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Properties;

@Log4j2
@Data
@Component
@PropertySource(value = "classpath:agent.properties")
@Configuration
public class AgentConfig {

  @Value("${cloud.url}")
  private String serverUrl;

  @Value("${local.server.url}")
  private String localServerUrl;

  @Value("${local.agent.register}")
  private Boolean localAgentRegister;

  @Value("${agent.version}")
  private String agentVersion;
  private String registered;
  private String UUID;
  @ToString.Exclude
  private String jwtApiKey;

  public AgentConfig() {
    try {
      touchConfigFile();
      String propertiesPath = PathUtil.getInstance().getConfigPath() + File.separator + "agent.properties";
      Properties properties = AgentConfig.loadProperties(new FileInputStream(propertiesPath));
      this.registered = properties.getProperty("agent.registered");
      this.UUID = properties.getProperty("agent.UUID");
      this.jwtApiKey = properties.getProperty("agent.jwtApiKey");
      log.info("Loaded agent config properties - " + this);
    } catch (FileNotFoundException | TestsigmaException e) {
      log.error(e.getMessage(), e);
    }
  }

  public static Properties loadProperties(InputStream is) throws TestsigmaException {
    Properties prop = new Properties();
    try {
      prop.load(is);
    } catch (final IOException e) {
      throw new TestsigmaException("Bad InputStream, failed to load properties from file", e);
    }
    return prop;

  }

  public Boolean getRegistered() {
    return BooleanUtils.toBoolean(this.registered);
  }

  private void touchConfigFile() {
    File configFile = new File(PathUtil.getInstance().getConfigPath() + File.separator + "agent.properties");
    try {
      FileUtils.touch(configFile);
    } catch (IOException e) {
      log.error("Error while creating agent configuration properties file: " + configFile.getAbsolutePath());
      log.error(e.getMessage(), e);
    }
  }

  /**
   * @throws TestsigmaException
   */
  public void saveConfig() throws TestsigmaException {

    FileOutputStream fileOut = null;
    touchConfigFile();
    try {
      String propertiesPath = PathUtil.getInstance().getConfigPath() + File.separator + "agent.properties";
      Properties properties = AgentConfig.loadProperties(new FileInputStream(propertiesPath));

      if (this.registered != null) {
        properties.setProperty("agent.registered", this.registered);
      }

      if (this.UUID != null) {
        properties.setProperty("agent.UUID", this.UUID);
      }

      if (this.jwtApiKey != null) {
        properties.setProperty("agent.jwtApiKey", this.jwtApiKey);
      }

      fileOut = new FileOutputStream(propertiesPath);
      properties.store(fileOut, "Agent configuration");
    } catch (IOException e) {
      throw new TestsigmaException(e);
    } finally {
      if (fileOut != null) {
        try {
          fileOut.flush();
          fileOut.close();
        } catch (IOException e) {
          throw new TestsigmaException("Failed to flush/close file out stream", e);
        }
      }
    }
  }

  /**
   * @throws TestsigmaException
   */
  public void removeConfig() throws TestsigmaException {

    FileOutputStream fileOut = null;
    touchConfigFile();
    try {
      String propertiesPath = PathUtil.getInstance().getConfigPath() + File.separator + "agent.properties";
      Properties properties = AgentConfig.loadProperties(new FileInputStream(propertiesPath));
      properties.remove("agent.UUID");
      properties.setProperty("agent.registered", "false");
      properties.remove("agent.jwtApiKey");

      fileOut = new FileOutputStream(propertiesPath);
      properties.store(fileOut, "Agent configuration");
    } catch (IOException e) {
      throw new TestsigmaException(e);
    } finally {
      if (fileOut != null) {
        try {
          fileOut.flush();
          fileOut.close();
        } catch (IOException e) {
          throw new TestsigmaException("Failed to flush/close file out stream", e);
        }
      }
    }
  }
}
