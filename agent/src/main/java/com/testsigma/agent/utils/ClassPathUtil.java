package com.testsigma.agent.utils;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

@Log4j2
public class ClassPathUtil {

  private static final String classPathSeparator = (SystemUtils.IS_OS_WINDOWS ? ";" : ":");

  public static String getClassPath() {
    StringBuilder classPath = new StringBuilder();
    String[] pathTokens = System.getProperty("java.class.path").split(System.getProperty("path.separator"));

    for (String token : pathTokens) {
      if (isClassPathJar(token)) {
        log.debug("Token matched as class path jar - " + token);
        setClassPathFromClassPathJar(token, classPath);
      } else if (isAgentJar(token)) {
        log.debug("Token matched as agent jar - " + token);
        setClassPathFromAgentJar(token, classPath);
      } else {
        File file = new File(token);
        if (file.isDirectory()) {
          setClasspathFromDirectory(token, classPath);
        } else {
          appendClassPathSeparator(classPath);
          classPath.append(token);
        }
      }
    }
    return classPath.toString();
  }

  public static String setClasspathFromDirectory(String path) {
    StringBuilder builder = new StringBuilder();
    setClasspathFromDirectory(path, builder);
    return builder.toString();
  }

  private static boolean isClassPathJar(String path) {
    return path.matches(".+/classpath[0-9]+.jar");
  }

  private static boolean isAgentJar(String path) {
    return path.matches(".+/agent.jar");
  }

  private static void setClassPathFromClassPathJar(String path, StringBuilder classPath) {
    try {
      FileInputStream in = new FileInputStream(path);
      JarInputStream jarStream = new JarInputStream(in);
      Manifest mf = jarStream.getManifest();
      String classPathStr = mf.getMainAttributes().getValue("Class-Path");
      String[] classPathTokens = classPathStr.split("file:");
      for (String token : classPathTokens) {
        appendClassPathSeparator(classPath);
        classPath.append(token.replace("file:", "").trim());
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  private static void setClassPathFromAgentJar(String path, StringBuilder classPath) {
    try {
      String pathPrefix = path.replace("agent.jar", "");
      log.debug("Checking the agent.jar file for classpath in - " + path);
      FileInputStream in = new FileInputStream(path);
      JarInputStream jarStream = new JarInputStream(in);
      Manifest mf = jarStream.getManifest();
      if (mf == null) {
        log.error("Unable to find manifest file in agent jar file");
        return;
      }
      String classPathStr = mf.getMainAttributes().getValue("Class-Path");
      if (classPathStr == null) {
        log.error("Unable to find class path value in manifest file in agent jar file");
        return;
      }
      String[] classPathTokens = classPathStr.split(" ");

      for (String token : classPathTokens) {
        if (token.endsWith(".jar")) {
          appendClassPathSeparator(classPath);
          classPath.append(pathPrefix).append(token);
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  private static void setClasspathFromDirectory(String path, StringBuilder classPath) {
    File file = new File(path);
    if (!file.isDirectory()) {
      return;
    }
    File[] jarList = file.listFiles();
    if (jarList != null) {
      for (File jar : jarList) {
        appendClassPathSeparator(classPath);
        classPath.append(path).append(File.separator).append(jar.getName());
      }
    }
  }

  private static void appendClassPathSeparator(StringBuilder classPath) {
    if (classPath.length() > 0) {
      classPath.append(classPathSeparator);
    }
  }
}
