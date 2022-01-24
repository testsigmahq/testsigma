package com.testsigma.service;

import com.google.common.collect.Lists;
import com.testsigma.config.StorageServiceFactory;
import com.testsigma.model.StorageAccessLevel;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.model.ProvisioningProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Log4j2
@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class CertificateService {
  public static final String CSR_EXTENSION = ".csr";
  public static final String CERTIFICATE_CER_EXTENSION = ".cer";
  public static final String CERTIFICATE_CRT_EXTENSION = ".crt";
  public static final String PEM_EXTENSION = ".pem";
  public static final String MOBILE_PROVISION_EXTENSION = ".mobileprovision";
  public static final String CSR_FILE_SUFFIX = "_csr";
  public static final String PRIVATE_KEY_FILE_SUFFIX = "_private_key";
  public static final String CERTIFICATE_FILE_SUFFIX = "_certificate";
  public static final String MOBILE_PROVISION_FILE_SUFFIX = "_mobileprovision";
  private final StorageServiceFactory storageServiceFactory;
  private final TestsigmaOSConfigService osService;
  private String certificateApiURL = null;

  @PostConstruct
  public void init() {
    certificateApiURL = osService.getTestsigmaOsProxyUrl() + "/api_public/ios/certificates/";
  }

  public void writeCSR(File csrOutput, File privateKeyOutPut) throws Exception {
    CloseableHttpClient closeableHttpClient = HttpClients.custom().build();
    HttpGet getRequest = new HttpGet(certificateApiURL + "csr");
    org.apache.http.HttpResponse response = closeableHttpClient.execute(getRequest);
    if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
      unzipFiles(response, csrOutput, privateKeyOutPut);
      log.info("csr and pem generated");
    } else {
      throw new TestsigmaException("Error while generating csr");
    }

  }

  public void writeCRT(File cer, File crtOutput) throws IOException, TestsigmaException {
    log.info("Generating CRT....");
    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    builder.addPart("certificate", new FileBody(cer, ContentType.DEFAULT_BINARY));
    HttpResponse response = postRequest(certificateApiURL + "cer", builder.build());
    Integer status = response.getStatusLine().getStatusCode();
    if (status.equals(HttpStatus.OK.value())) {
      writeResponseToFile(response, crtOutput);
      log.info("crt generated");
    } else {
      throw new TestsigmaException("Error while generating CRT");
    }

  }

  public void writePem(File crt, File pemOutput) throws IOException, TestsigmaException {
    log.info("Generating Certificate PEM....");
    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    builder.addPart("certificate", new FileBody(crt, ContentType.DEFAULT_BINARY));
    HttpResponse response = postRequest(certificateApiURL + "cer_pem", builder.build());
    Integer status = response.getStatusLine().getStatusCode();
    if (status.equals(HttpStatus.OK.value())) {
      writeResponseToFile(response, pemOutput);
      log.info("Private key for certificate(CRT) updated successfully");
    } else {
      log.error("Error while updating private key for CRT");
      throw new TestsigmaException("Error while updating private key for CRT");
    }
  }

  public void setPreSignedURLs(ProvisioningProfile provisioningProfile) {
    String profilePathPrefix = s3Prefix(provisioningProfile.getId());
    String csrFile = profilePathPrefix + CSR_FILE_SUFFIX + CSR_EXTENSION;
    String privateKeyFile = profilePathPrefix + PRIVATE_KEY_FILE_SUFFIX + PEM_EXTENSION;
    String certificateCerFile = profilePathPrefix + CERTIFICATE_FILE_SUFFIX + CERTIFICATE_CER_EXTENSION;
    String certificateCrtFile = profilePathPrefix + CERTIFICATE_FILE_SUFFIX + CERTIFICATE_CRT_EXTENSION;
    String certificatePemFile = profilePathPrefix + CERTIFICATE_FILE_SUFFIX + PEM_EXTENSION;
    String mobileProvisionFile = profilePathPrefix + MOBILE_PROVISION_FILE_SUFFIX + MOBILE_PROVISION_EXTENSION;
    StorageService storageService = storageServiceFactory.getStorageService();
    provisioningProfile.setCsrPresignedUrl(storageService.generatePreSignedURLIfExists(csrFile,
      StorageAccessLevel.READ, 180).orElse(null));

    provisioningProfile.setPrivateKeyPresignedUrl(storageService.generatePreSignedURLIfExists(privateKeyFile,
      StorageAccessLevel.READ, 180).orElse(null));

    provisioningProfile.setCertificateCerPresignedUrl(storageService.generatePreSignedURLIfExists(certificateCerFile,
      StorageAccessLevel.READ, 180).orElse(null));

    provisioningProfile.setCertificateCrtPresignedUrl(storageService.generatePreSignedURLIfExists(certificateCrtFile,
      StorageAccessLevel.READ, 180).orElse(null));

    provisioningProfile.setCertificatePemPresignedUrl(storageService.generatePreSignedURLIfExists(certificatePemFile,
      StorageAccessLevel.READ, 180).orElse(null));

    provisioningProfile.setProvisioningProfilePresignedUrl(storageService.generatePreSignedURLIfExists(mobileProvisionFile,
      StorageAccessLevel.READ, 180).orElse(null));
  }

  public String s3Prefix(Long profileId) {
    return "provisioning_profiles/" + profileId;
  }

  public List<File> unzipFiles(HttpResponse response, File csrOutput, File privateKeyOutput) throws IOException {
    List<File> res = new ArrayList<>();
    res.add(csrOutput);
    res.add(privateKeyOutput);
    File fileZip = File.createTempFile("csrAndPemopensource", ".zip");
    BufferedInputStream bis = new BufferedInputStream(response.getEntity().getContent());
    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileZip));
    int inByte;
    while ((inByte = bis.read()) != -1) bos.write(inByte);
    bis.close();
    bos.close();
    byte[] buffer = new byte[1024];
    ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
    ZipEntry zipEntry = zis.getNextEntry();
    int i = 0;
    while (zipEntry != null) {
      File newFile = res.get(i++);
      FileOutputStream fos = new FileOutputStream(newFile);
      int len;
      while ((len = zis.read(buffer)) > 0) {
        fos.write(buffer, 0, len);
      }
      fos.close();
      zipEntry = zis.getNextEntry();
    }
    zis.closeEntry();
    zis.close();
    return res;
  }

  public HttpResponse postRequest(String url, HttpEntity body) throws IOException {
    CloseableHttpClient closeableHttpClient = HttpClients.custom().build();
    Header accept = new BasicHeader(HttpHeaders.ACCEPT, "*/*");
    List<Header> headers = Lists.newArrayList(accept);
    HttpPost postRequest = new HttpPost(url);
    postRequest.setHeaders(headers.toArray(Header[]::new));
    postRequest.setEntity(body);
    return closeableHttpClient.execute(postRequest);
  }

  public void writeResponseToFile(HttpResponse response, File file) throws IOException {
    BufferedInputStream bis = new BufferedInputStream(response.getEntity().getContent());
    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
    int inByte;
    while ((inByte = bis.read()) != -1) bos.write(inByte);
    bis.close();
    bos.close();
  }

}
