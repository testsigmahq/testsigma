package com.testsigma.automator.service;


import com.testsigma.automator.http.HttpClient;
import com.testsigma.automator.http.HttpResponse;
import com.testsigma.automator.runners.EnvironmentRunner;
import com.testsigma.automator.utilities.PathUtil;
import com.testsigma.sdk.Element;
import com.testsigma.sdk.Logger;
import com.testsigma.sdk.RunTimeData;
import com.testsigma.sdk.TestData;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.openqa.selenium.By;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;

@Data
@Log4j2
public class KibbutzService {
  private Class<?> uiIdentifierClass;
  private Class<?> testDataClass;
  private Class<?> loggerClass;
  private Class<?> runTimeDataClass;
  private Class<?> testDataParameterClass;
  private URLClassLoader jarFileLoader;

  private static KibbutzService _instance = null;

  public static  KibbutzService getInstance() {
    if (_instance == null)
      _instance = new KibbutzService();
    return _instance;
  }

  public String checkAndDownloadJar(String classPath, String modifiedHash) throws IOException {
    log.info("Kibbutz Plugin For Processing Step :::"+ classPath);
    String fileName = FilenameUtils.getName(classPath);
    if (fileName.indexOf("?") > 0) {
      fileName = fileName.substring(0, fileName.indexOf("?"));
    }
    String jarFilePath = Paths.get(PathUtil.getInstance().getCustomClassesPath(),
      modifiedHash, fileName).toFile().getAbsolutePath();
    File jarFile = new File(jarFilePath);

    if (!jarFile.exists()) {
      log.info(String.format("Jar File Doesn't Exists. Downloading from %s to %s",
        classPath, jarFile.getAbsolutePath()));
      if (jarFile.getParentFile() != null) {
        jarFile.getParentFile().mkdirs();
      }
      HttpClient httpClient = EnvironmentRunner.getAssetsHttpClient();
      HttpResponse<String> response = httpClient.downloadFile(classPath, jarFilePath);
      log.info(String.format("Response while downloading jar file: %s - %s  ",
        response.getStatusCode(), response.getStatusMessage()));
      if (!jarFile.exists()) {
        log.error("Failed to download zip file to location - " + jarFile.getAbsolutePath());
      }
    } else {
      log.info(String.format("Jar file exists at %s so skipping download of custom NLP Jar", jarFile.getAbsolutePath()));
    }
    return jarFilePath;
  }

  public Class<?> loadJarClass(String jarFilePath, String fullyQualifiedName, Boolean isNlp) throws MalformedURLException, ClassNotFoundException {
    log.info(String.format("Loading main class %s from jar file from location %s",
      fullyQualifiedName, jarFilePath));
    File jarFile = new File(jarFilePath);
    this.jarFileLoader = URLClassLoader.newInstance(
      new URL[]{jarFile.toURI().toURL()},
      getClass().getClassLoader()
    );
    if(isNlp){
      loadSdkClassesNlpClasses();
    }else
      loadSdkTestDataFunctionClasses();
    return loadClassFromJar(fullyQualifiedName);
  }

  private void loadSdkTestDataFunctionClasses() throws ClassNotFoundException {
    log.info("Initiating Test Data Function classes for execution");
    this.testDataParameterClass = loadClassFromJar("com.testsigma.sdk.TestDataParameter");
  }

  private void loadSdkClassesNlpClasses() throws ClassNotFoundException {
    log.info("Initiating NLP classes for execution");
    this.uiIdentifierClass = loadClassFromJar(Element.class.getName());
    this.testDataClass = loadClassFromJar(TestData.class.getName());
    this.loggerClass = loadClassFromJar(Logger.class.getName());
    this.runTimeDataClass = loadClassFromJar(RunTimeData.class.getName());
  }

  public Class<?> loadClassFromJar(String className) throws ClassNotFoundException {
    return Class.forName(className, true, jarFileLoader );
  }

  public Object getElementInstance(String definition, By by) throws NoSuchMethodException,
    InvocationTargetException, InstantiationException, IllegalAccessException {
    return uiIdentifierClass.getDeclaredConstructor(String.class, By.class)
      .newInstance(definition, by);
  }

  public Object getTestDataInstance(Object value) throws NoSuchMethodException, InvocationTargetException,
    InstantiationException, IllegalAccessException {
    return testDataClass.getDeclaredConstructor(Object.class).newInstance(value);
  }

  public Object getRunTimeDataInstance() throws NoSuchMethodException, InvocationTargetException,
    InstantiationException, IllegalAccessException {
    return runTimeDataClass.getDeclaredConstructor().newInstance();
  }

  public Object getTestDataParameterInstance(Object value) throws NoSuchMethodException, InvocationTargetException,
    InstantiationException, IllegalAccessException {
    return testDataParameterClass.getDeclaredConstructor(Object.class).newInstance(value);
  }

}
