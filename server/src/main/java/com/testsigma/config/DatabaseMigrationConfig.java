package com.testsigma.config;


import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.internal.resolver.ChecksumCalculator;
import org.flywaydb.core.internal.resource.filesystem.FileSystemResource;
import org.flywaydb.core.internal.scanner.classpath.FileSystemClassPathLocationScanner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@Configuration
public class DatabaseMigrationConfig {
  private static final String SCHEMA_PATH_RESOURCE = "classpath:db/bootstrap";
  private static final String MIGRATION_PATH = "db/migration/";
  private static final String VERSION_SEPARATOR = "__";
  private static final String VERSION_CHAR = "V";
  private static final String INSERT_HISTORY = "INSERT INTO `flyway_schema_history` (`installed_rank`, `version`, `description`, `type`, `script`, `checksum`, `installed_by`,  `execution_time`, `success`)\n" +
    "VALUES (?, ?, ?, 'SQL', ?, ?, ?,20, 1)";
  private static final String DELETE_HISTORY = "DELETE FROM flyway_schema_history";
  private static final String CREATE_DATABASE = "CREATE DATABASE IF NOT EXISTS `%s` /*!40100 DEFAULT CHARACTER SET utf8mb4 */";
  @Value("${spring.datasource.url}")
  private String dataSourceUrl;
  @Value("${spring.datasource.username}")
  private String dataSourceUser;
  @Value("${spring.datasource.password}")
  private String dataSourcePassword;
  private Connection connection;

  @PostConstruct
  public void postConstruct() throws Exception {
    log.info("Setting up database");
    setUpDatabase();
  }

  private void setUpDatabase() throws Exception {
    Flyway flyway;
    try {
      connection = getConnection();
      if (isNewDatabase()) {
        executeBootStrap();
        addMigrationEntries();
      } else {
        flyway = Flyway.configure().dataSource(dataSourceUrl, dataSourceUser, dataSourcePassword).placeholderReplacement(false).load();
        flyway.migrate();
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw e;
    } finally {
      if (connection != null)
        connection.close();
    }
  }

  /*
  If db is up to date, we just add migration entries if any without executing SQLs
   */
  private void addMigrationEntries() throws SQLException {
    SortedMap<Integer, String> versionAndFileMap = getVersionAndFileMap();
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    int installedRank = 1;
    for (Integer version : versionAndFileMap.keySet()) {
      String resourceFilePath = versionAndFileMap.get(version);
      int checksum = getChecksum(classLoader.getResource(resourceFilePath).getFile());
      addMigrationEntryToHistory(installedRank, version, resourceFilePath, checksum);
      installedRank++;
    }
  }

  private void executeBootStrap() throws SQLException {
    log.info("Executing bootstrap");
    Flyway flyway = Flyway.configure().dataSource(dataSourceUrl, dataSourceUser, dataSourcePassword)
      .locations(SCHEMA_PATH_RESOURCE)
      .placeholderReplacement(false)
      .load();
    flyway.migrate();
    clearHistoryForSchema();
  }

  private void clearHistoryForSchema() throws SQLException {
    Statement stmt = connection.createStatement();
    stmt.executeUpdate(DELETE_HISTORY);
  }

  private boolean isNewDatabase() throws SQLException {
    ResultSet resultSet = connection.createStatement().executeQuery("SHOW TABLES");
    return !(resultSet.next());
  }

  private int getChecksum(String absoluteFilePath) {
    FileSystemResource r = new FileSystemResource(null, absoluteFilePath, Charset.forName("UTF-8"), false);
    return ChecksumCalculator.calculate(r);
  }

  private SortedMap<Integer, String> getVersionAndFileMap() {
    SortedMap<Integer, String> sortedByVersion = new TreeMap<>();//We need to sort by version for DB entry
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    URL url = classLoader.getResource(MIGRATION_PATH);
    if (!isMigrationExists(url)) {
      return sortedByVersion;
    }
    FileSystemClassPathLocationScanner classPathLocationScanner = new FileSystemClassPathLocationScanner();
    Set<String> resourceFilePaths = classPathLocationScanner.findResourceNames(MIGRATION_PATH, url);
    for (String resourceFilePath : resourceFilePaths) {
      sortedByVersion.put(getVersionFromFilePath(resourceFilePath), resourceFilePath);
    }
    return sortedByVersion;
  }

  private boolean isMigrationExists(URL fileUrl) {
    if (fileUrl == null) {
      return false;
    }
    File file = new File(fileUrl.getPath());
    if (file.exists() && file.isDirectory()) {
      File[] files = file.listFiles();
      return (files.length > 0);
    }
    return false;
  }

  private String getFilePath(String resourceFilePath) {
    String fileName = resourceFilePath.replaceAll(MIGRATION_PATH, "");
    return fileName;
  }

  private Integer getVersionFromFilePath(String resourceFilePath) {
    URL fileURL = Thread.currentThread().getContextClassLoader().getResource(resourceFilePath);
    String fileName = new File(fileURL.getPath()).getName();
    String versionString = StringUtils.substringBetween(fileName, VERSION_CHAR, VERSION_SEPARATOR);
    return Integer.parseInt(versionString);
  }

  private void addMigrationEntryToHistory(int installedRank, Integer version, String resourceFilePath, int checksum) throws SQLException {
    String fileName = getFilePath(resourceFilePath);
    String description = getFileDescription(fileName);
    log.info("Adding migration entry for:" + resourceFilePath);
    PreparedStatement preparedStatement = connection.prepareStatement(INSERT_HISTORY);
    preparedStatement.setInt(1, installedRank);
    preparedStatement.setString(2, version.toString());
    preparedStatement.setString(3, description);
    preparedStatement.setString(4, fileName);
    preparedStatement.setInt(5, checksum);
    preparedStatement.setString(6, dataSourceUser);
    int i = preparedStatement.executeUpdate();
    if (i == 0) {
      throw new SQLException("Unable to update migration entry for sql file:" + resourceFilePath);
    }
  }

  private String getFileDescription(String fileName) {
    String versionTruncated = fileName.substring(fileName.indexOf(VERSION_SEPARATOR) + 2, fileName.indexOf("."));
    return versionTruncated.replaceAll("_", " ");
  }

  private Connection getConnection() throws SQLException {
    if (connection == null) {
      try {
        connection = DriverManager.getConnection(dataSourceUrl, dataSourceUser, dataSourcePassword);
      } catch (SQLSyntaxErrorException e) {
        if((e.getSQLState().equals("42000")) && e.getMessage().startsWith("Unknown database")) {
          log.info("Database does not exist, creating it");
          String dbName = parseMysqlDatabaseName(dataSourceUrl);
          createDatabase(dbName);
          connection = DriverManager.getConnection(dataSourceUrl, dataSourceUser, dataSourcePassword);
        }
      }
    }
    return connection;
  }

  private void createDatabase(String dbName) throws SQLException {
    log.info("Trying to create a database - " + dbName);
    String mysqlHostName = parseMysqlHostName(dataSourceUrl);
    String parseMysqlPort = parseMysqlPort(dataSourceUrl);
    String connectionUrl = "jdbc:mysql://" + mysqlHostName + ":" + parseMysqlPort + "?useSSL=false";
    Connection connection1 = null;
    try {
      log.info("Establishing connection to mysql server - " + connectionUrl);
      connection1 = DriverManager.getConnection(connectionUrl, dataSourceUser, dataSourcePassword);
      Statement stmt = connection1.createStatement();
      stmt.executeUpdate(String.format(CREATE_DATABASE, dbName));
    } catch (SQLException e) {
      log.error("Error creating database", e);
    } finally {
      if (connection1 != null) {
        connection1.close();
      }
    }
  }

  private String parseMysqlDatabaseName(String url) {
    String dbName = "testsigma_opensource";
    try {
      Pattern pattern = Pattern.compile("^jdbc:mysql:\\/\\/(.+):([0-9]+)\\/(.+)\\?useSSL=false$");
      Matcher matcher = pattern.matcher(url);
      if(matcher.matches()) {
        dbName = matcher.group(3);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return dbName;
  }

  private String parseMysqlHostName(String url) {
    String dbName = "localhost";
    try {
      Pattern pattern = Pattern.compile("^jdbc:mysql:\\/\\/(.+):([0-9]+)\\/(.+)\\?useSSL=false$");
      Matcher matcher = pattern.matcher(url);
      if(matcher.matches()) {
        dbName = matcher.group(1);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return dbName;
  }

  private String parseMysqlPort(String url) {
    String dbName = "3306";
    try {
      Pattern pattern = Pattern.compile("^jdbc:mysql:\\/\\/(.+):([0-9]+)\\/(.+)\\?useSSL=false$");
      Matcher matcher = pattern.matcher(url);
      if(matcher.matches()) {
        dbName = matcher.group(2);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return dbName;
  }
}
