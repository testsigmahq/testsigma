package com.testsigma.config;

import com.testsigma.exception.TestsigmaException;
import com.testsigma.model.AuthenticationType;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.Properties;

@Log4j2
@Configuration
@Data
@Component
public class AdditionalPropertiesConfig {

  @Value("${authentication.type}")
  private AuthenticationType authenticationType;

  private String googleClientId;
  private String googleClientSecret;
  private String userName;
  private String password;

  @Value("${authentication.google.clientId}")
  private String defaultGoogleClientId;
  @Value("${authentication.google.clientSecret}")
  private String defaultGoogleClientSecret;
  @Value("${authentication.form.username}")
  private String defaultUserName;
  @Value("${authentication.form.password}")
  private String defaultPassword;

  @Value("${authentication.api.key}")
  private String apiKey;
  @Value("${authentication.jwt.secret}")
  private String jwtSecret;
  @Value("${authentication.api.enabled}")
  private Boolean isApiEnabled;
  @Value("${ts.root.dir}")
  private String testsigmaDataPath;

  public static Properties loadProperties(InputStream is) throws TestsigmaException {
    Properties prop = new Properties();
    try {
      prop.load(is);
    } catch (final IOException e) {
      throw new TestsigmaException("Bad InputStream, failed to load properties from file", e);
    }
    return prop;

  }

  @PostConstruct
  public void init() {
    try {
      touchConfigFile();
      log.info("Using authentication.properties file from - " + testsigmaDataPath +" - "+ System.getProperty("TS_DATA_DIR"));
      String propertiesPath = testsigmaDataPath + File.separator + "authentication.properties";
      Properties properties = AdditionalPropertiesConfig.loadProperties(new FileInputStream(propertiesPath));
      this.populateMissingValues(properties);
      this.saveConfig();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }

  }

  private void touchConfigFile() {
    log.info("Touching authentication.properties file from - " + testsigmaDataPath +" - "+ System.getProperty("TS_DATA_DIR"));
    File configFile = new File(testsigmaDataPath + File.separator + "authentication.properties");
    try {
      FileUtils.touch(configFile);
    } catch (IOException e) {
      log.error("Error while creating auth configuration properties file: " + configFile.getAbsolutePath());
      log.error(e.getMessage(), e);
    }
  }

  public void saveConfig() throws TestsigmaException {

    FileOutputStream fileOut = null;
    touchConfigFile();
    try {
      String propertiesPath = testsigmaDataPath + File.separator + "authentication.properties";
      log.info("Saving authentication.properties file to - " + propertiesPath);
      Properties properties = AdditionalPropertiesConfig.loadProperties(new FileInputStream(propertiesPath));
      properties.setProperty("authentication.google.clientId", ObjectUtils.defaultIfNull(this.googleClientId, ""));
      properties.setProperty("authentication.google.clientSecret", ObjectUtils.defaultIfNull(this.googleClientSecret, ""));
      properties.setProperty("authentication.form.username", ObjectUtils.defaultIfNull(this.userName, ""));
      properties.setProperty("authentication.form.password", ObjectUtils.defaultIfNull(this.password, ""));
      properties.setProperty("authentication.jwt.secret", this.jwtSecret);
      properties.setProperty("authentication.api.key", ObjectUtils.defaultIfNull(this.apiKey, ""));
      properties.setProperty("authentication.api.enabled", String.valueOf(this.isApiEnabled));
      properties.setProperty("authentication.type", this.authenticationType.name());
      fileOut = new FileOutputStream(propertiesPath);
      properties.store(fileOut, "Authentication configuration");
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

  private void populateMissingValues(Properties properties) throws TestsigmaException, FileNotFoundException {
    if (properties != null) {
      userName = properties.getProperty("authentication.form.username", this.defaultUserName);
      password = properties.getProperty("authentication.form.password", this.defaultPassword);
      apiKey = properties.getProperty("authentication.api.key", this.apiKey);
      jwtSecret = properties.getProperty("authentication.jwt.secret", this.jwtSecret);
      isApiEnabled = Boolean.parseBoolean(properties.getProperty("authentication.api.enabled", String.valueOf(this.isApiEnabled)));
      authenticationType = AuthenticationType.valueOf(properties.getProperty("authentication.type", this.authenticationType.name()));
      googleClientId = properties.getProperty("authentication.google.clientId", this.defaultGoogleClientId);
      googleClientSecret = properties.getProperty("authentication.google.clientSecret", this.defaultGoogleClientSecret);
    }
  }
}
