package com.testsigma.util;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

@Log4j2
public class JarExtractorUtil {

    public Set<File> extractJarAndCreateTempFiles(URL jarURL, String subFolderPath, String fileExtension) throws IOException {
        log.debug(String.format("Finding files in jar subdirectory:%s with extension %s",subFolderPath,fileExtension));

        Set<File> classpathResourceTempFiles = new TreeSet<>();
        URLConnection con = jarURL.openConnection();
        JarURLConnection jarCon = (JarURLConnection) con;
        JarFile jarFile = jarCon.getJarFile();
        Enumeration jarEntries = jarFile.entries();
        ClassLoader cl = this.getClass().getClassLoader();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) jarEntries.nextElement();
            String jarEntryName = jarEntry.getName();
            if (jarEntryName.startsWith(subFolderPath) && !jarEntry.isDirectory() && jarEntryName.toLowerCase().endsWith(fileExtension)) {
                log.debug("Found a matching extension file::"+jarEntryName);
                jarEntryName = jarEntryName.endsWith(File.separator)?jarEntryName.substring(0,jarEntryName.length()-1):jarEntryName;
                Resource resource = resolver.getResource("classpath:" + jarEntryName);
                String text = new BufferedReader(
                        new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining("\n"));
                String fileName = new File(jarEntryName).getName();
                File tempFile = File.createTempFile(fileName,fileExtension);
                FileUtils.writeStringToFile(tempFile,text, Charset.defaultCharset());
                log.debug("Adding temporary file path:"+tempFile.getAbsolutePath());
                classpathResourceTempFiles.add(tempFile);
            }
        }
        return classpathResourceTempFiles;
    }
}
