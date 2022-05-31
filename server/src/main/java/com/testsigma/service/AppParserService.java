package com.testsigma.service;

import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListFormatException;
import com.dd.plist.PropertyListParser;
import com.testsigma.model.UploadVersionAppInfo;
import lombok.extern.log4j.Log4j2;
import net.dongliu.apk.parser.ApkFile;
import net.dongliu.apk.parser.bean.ApkMeta;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.testsigma.constants.Constants.*;

@Service
@Log4j2
public class AppParserService {

  public UploadVersionAppInfo parseFile(File file){
    if (checkIsItIpaFile(file)){
      File ipaInfo = parseIpaFile(file);
      NSDictionary rootDict = null;
      if (ipaInfo != null) {
        try {
          rootDict = (NSDictionary) PropertyListParser.parse(ipaInfo);
        } catch (IOException | PropertyListFormatException | ParseException | ParserConfigurationException | SAXException e) {
          e.printStackTrace();
        }
      }
      return new UploadVersionAppInfo(null, null,
        rootDict.objectForKey(BUNDLE_VERSION).toString(), rootDict.objectForKey(BUNDLE_IDENTIFIER).toString());
    }else if(checkIsItApkFile(file)){
      return parseApkFile(file);
    }
    return new UploadVersionAppInfo(null,null,null,null);
  }

  private boolean checkIsItApkFile(File file) {
    String filePath = file.getAbsolutePath();
    int lastIndex = filePath.lastIndexOf(DOT);
    boolean isApkFile = filePath.substring(lastIndex+1).equalsIgnoreCase(".apk") ||
            filePath.substring(lastIndex+1).equalsIgnoreCase(".xapk") ||
            filePath.substring(lastIndex+1).equalsIgnoreCase(".apks") ||
            filePath.substring(lastIndex+1).equalsIgnoreCase(".apkm");
    return isApkFile;
  }

  private UploadVersionAppInfo parseApkFile(File file) {
    ApkMeta apkMeta = null;
    String launchActivity = null;
    try {
      ApkFile apkFileOut = new ApkFile(file);
      apkMeta = apkFileOut.getApkMeta();
      launchActivity = getLaunchActivity(apkFileOut);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new UploadVersionAppInfo(launchActivity, apkMeta.getPackageName(),
      apkMeta.getVersionName(), null);
  }

  private String getLaunchActivity(ApkFile apkFile) {

    String binaryManifestFile;
    String launchActivity = null;
    try {
      binaryManifestFile = apkFile.getManifestXml();
      DocumentBuilder documentBuilder =
        DocumentBuilderFactory.newInstance().newDocumentBuilder();
      Document doc =
        documentBuilder.parse(new InputSource(new StringReader(binaryManifestFile)));
      doc.getDocumentElement().normalize();
      NodeList nodeList = doc.getElementsByTagName(ACTIVITY_NODE);
      launchActivity = extractLaunchActivity(nodeList);
    } catch (IOException | ParserConfigurationException | SAXException e) {
      e.printStackTrace();
    }
    return launchActivity;
  }

  private String extractLaunchActivity(NodeList nodeList) {

    String launchActivity=null;
    outerloop:
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      log.info("Node name" + node.getNodeName());
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element element = (Element) node;
        NodeList childNodeList = element.getElementsByTagName(INTENT_FILTER_NODE);
        for (int j = 0; j < childNodeList.getLength(); j++) {
          Node childNode = childNodeList.item(j);
          Element childElement = (Element) childNode;
          NodeList subChildNodeList = childElement.getElementsByTagName(CATEGORY_NODE);
          for (int k = 0; k < subChildNodeList.getLength(); k++) {
            log.info("element value" + subChildNodeList.item(k).getTextContent());
            if (subChildNodeList.item(k).getAttributes().item(0).getNodeValue().equals(LAUNCHER_VALUE)) {
              launchActivity = node.getAttributes().getNamedItem(LAUNCHABLE_ACTIVITY_ATTRIBUTE).getNodeValue();
              break outerloop;
            }
          }
        }
      }
    }
    return launchActivity;
  }


  private boolean checkIsItIpaFile(File ipaFile) {
    String filePath = ipaFile.getAbsolutePath();
    int lastIndex = filePath.lastIndexOf(DOT);
    return filePath.substring(lastIndex+1).equalsIgnoreCase("ipa");
  }

  private File parseIpaFile(File ipaFile) {
    try {
      String zipFile = ipaFile.getAbsolutePath().replaceAll(".ipa", ".zip");
      File newFile = new File(zipFile);
      return checkIpaFileExistAndCreateZipFile(ipaFile, newFile);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ipaFile;
  }

  private File checkIpaFileExistAndCreateZipFile(File ipaFile, File newFile) {
    File ipaFileOutPut = null;
    if (ipaFile.exists()) {
      //Create a Zip file
      createZipFile(ipaFile, newFile);
      try {
        ipaFileOutPut = parseZipFile(newFile, newFile.getParent());
      } catch (IOException e) {
        log.error("error while parsing the file");
        e.printStackTrace();
      }
    }
    return ipaFileOutPut;
  }


  private void createZipFile(File ipaFile, File newFile) {
    int byteRead;
    InputStream inStream;
    FileOutputStream fs;
    try {
      inStream = new FileInputStream(ipaFile);
      fs = new FileOutputStream(newFile);
      byte[] buffer = new byte[1444];
      while ((byteRead = inStream.read(buffer)) != -1) {
        fs.write(buffer, 0, byteRead);
      }
      inStream.close();
      fs.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static File parseZipFile(File file, String unzipDirectory) throws IOException {
    File unzipFile;
    File infoPlistFileOutput = null;
    ZipFile zipFile = null;
    try {
      zipFile = new ZipFile(file);
      unzipFile = deleteExistingDirAndCreateNewOne(file,unzipDirectory);
      //Get zip file entry enumeration object
      infoPlistFileOutput = processZipFileAndGetInfoPlistFile(zipFile,
        unzipFile);
    } catch (IOException ex) {
      ex.printStackTrace();
    } finally {
      if (zipFile != null) {
        zipFile.close();
      }
    }
    if (file.exists()) {
      file.delete();
    }
    return infoPlistFileOutput;
  }

  private static File deleteExistingDirAndCreateNewOne(File file, String unzipDirectory) {

    File unzipFile;
    String name = file.getName().substring(0, file.getName().lastIndexOf("."));
    unzipFile = new File(unzipDirectory + "/" + name);
    if (unzipFile.exists()) {
      unzipFile.delete();
    }
    unzipFile.mkdir();

    return unzipFile;

  }

  private static File processZipFileAndGetInfoPlistFile(ZipFile zipFile,
                                                        File unzipFile) throws IOException {
    Enumeration<? extends ZipEntry> zipEnum = zipFile.entries();
    //define object
    ZipEntry entry;String entryName;
    File infoPlistFileOutput = null;
    //loop reading entries
    while (zipEnum.hasMoreElements()) {
      //get the current entry
      entry = zipEnum.nextElement();
      entryName = entry.getName();
      infoPlistFileOutput = writeToInfoPlistFile(entryName,zipFile,
        entry,unzipFile,infoPlistFileOutput);
    }
    return infoPlistFileOutput;
  }

  private static File writeToInfoPlistFile(String entryName, ZipFile zipFile, ZipEntry entry, File unzipFile, File infoPlistFileOutput) {

    InputStream inputStream;
    OutputStream outputStream;
    int length = entryName.split("\\/").length;
    try {
      for (int v = 0; v < length; v++) {
        if (entryName.endsWith(".app/Info.plist")) {//is Info.plist file, then output to file
          inputStream = zipFile.getInputStream(entry);
          infoPlistFileOutput = new File(unzipFile.getAbsolutePath() + "/Info.plist");
          outputStream = new FileOutputStream(infoPlistFileOutput);
          byte[] buffer = new byte[1024 * 8];
          int readLen;
          while ((readLen = inputStream.read(buffer, 0, 1024 * 8)) != -1) {
            outputStream.write(buffer, 0, readLen);
          }
          break;
        }
      }
    }catch (IOException ie){
      log.error ("Error while writing to infoPlist file" + ie.getMessage(), ie);
      ie.printStackTrace();
    }
    return infoPlistFileOutput;
  }
}
