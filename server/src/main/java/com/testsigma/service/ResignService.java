package com.testsigma.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.testsigma.config.StorageServiceFactory;
import com.testsigma.config.URLConstants;
import com.testsigma.model.StorageAccessLevel;
import com.testsigma.model.StorageType;
import com.testsigma.dto.ResignRequestUsingUrlsDTO;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.model.ProvisioningProfile;
import com.testsigma.model.Upload;
import com.testsigma.util.HttpClient;
import com.testsigma.util.HttpResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class ResignService {
  private static final String ISIGN_USING_FILES_URL = "/api/sign/files";
  private static final String ISIGN_USING_URLS_URL = "/api/sign/urls";

  private final ProvisioningProfileUploadService provisioningProfileUploadService;
  private final HttpClient httpClient;
  private final CertificateService certificateService;
  private final UploadService uploadService;
  private final StorageServiceFactory storageServiceFactory;
  private final TestsigmaOSConfigService testsigmaOSConfigService;

  public void reSignWda(ProvisioningProfile profile) throws TestsigmaException {
    log.info(String.format("Resigning wda.ipa file for provisioning profile [%s] - [%s]", profile.getId(),
      profile.getName()));
    certificateService.setPreSignedURLs(profile);
    String ipaName = profile.getId() + "_wda";
    String resignedPathPrefix = "wda/" + profile.getId() + "/wda.ipa";
    if (storageServiceFactory.getStorageService().getStorageType() == StorageType.ON_PREMISE) {
      File resignedIpa = resignUsingFiles(profile, ipaName, commonWdaS3Path());
      storageServiceFactory.getStorageService().addFile(resignedPathPrefix, resignedIpa);
    } else {
      resignUsingUrls(profile, ipaName, commonWdaS3Path(),
        storageServiceFactory.getStorageService().generatePreSignedURL(resignedPathPrefix, StorageAccessLevel.WRITE, 60));
    }
  }

  public void reSignUpload(ProvisioningProfile profile, Upload upload) throws TestsigmaException {
    log.info(String.format("Resigning Upload [%s] - [%s] file for provisioning profile [%s] - [%s]",
      upload.getId(), upload.getName(), profile.getId(), profile.getName()));

    certificateService.setPreSignedURLs(profile);
    String ipaName = profile.getId() + "_" + upload.getId();
    String resignedPathPrefix = upload.getResignedAppS3PathSuffix(profile.getId());

    try {
      if (storageServiceFactory.getStorageService().getStorageType() == StorageType.ON_PREMISE) {
        File resignedIpa = resignUsingFiles(profile, ipaName, new URL(uploadService.getPreSignedURL(upload)));
        storageServiceFactory.getStorageService().addFile(resignedPathPrefix, resignedIpa);
      } else {
        resignUsingUrls(profile, ipaName, new URL(uploadService.getPreSignedURL(upload)),
          storageServiceFactory.getStorageService().generatePreSignedURL(resignedPathPrefix, StorageAccessLevel.WRITE, 60));
      }
    } catch (MalformedURLException e) {
      log.error(e.getMessage(), e);
      throw new TestsigmaException(e.getMessage(), e);
    }
  }

  public void resignPublicUrlApp(ProvisioningProfile profile, String presignedAppUrl, String resignedPathSuffix)
    throws TestsigmaException {
    log.info(String.format("Resigning App [%s] - [%s] file for provisioning profile [%s] - [%s]",
      presignedAppUrl, resignedPathSuffix, profile.getId(), profile.getName()));
    try {
      certificateService.setPreSignedURLs(profile);
      String ipaName = profile.getId() + "_" + FilenameUtils.getBaseName(new URL(presignedAppUrl).getPath());
      File resignedIpa = resignUsingFiles(profile, ipaName, new URL(presignedAppUrl));
      storageServiceFactory.getStorageService().addFile(resignedPathSuffix, resignedIpa);
    } catch (Exception e) {
      throw new TestsigmaException(e.getMessage(), e);
    }
  }

  public void reSignForAllProfiles(Upload upload, List<ProvisioningProfile> profiles) throws TestsigmaException {
    provisioningProfileUploadService.removeEntitiesForUpload(upload);
    for (ProvisioningProfile profile : profiles) {
      reSignUpload(profile, upload);
      provisioningProfileUploadService.create(profile, upload);
    }
  }

  public void reSignAllUploads(ProvisioningProfile profile, List<Upload> uploads)
    throws TestsigmaException {
    provisioningProfileUploadService.removeEntitiesForProfile(profile);
    for (Upload upload : uploads) {
      try {
        reSignUpload(profile, upload);
        provisioningProfileUploadService.create(profile, upload);
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }
  }

  public URL commonWdaS3Path() throws TestsigmaException {
    ArrayList<Header> headers = new ArrayList<>();
    headers.add(new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"));
    HttpResponse<String> response = httpClient.get(testsigmaOSConfigService.getUrl() +
      URLConstants.TESTSIGMA_OS_PUBLIC_WDA_URL, headers, new TypeReference<>() {
    });
    try {
      return new URL(response.getResponseEntity());
    } catch (MalformedURLException e) {
      throw new TestsigmaException(e.getMessage(), e);
    }
  }

  private List<Header> getResignHeaders() {
    Header authorization = new BasicHeader(org.apache.http.HttpHeaders.AUTHORIZATION, "Bearer " + testsigmaOSConfigService.find().getAccessKey());
    Header accept = new BasicHeader(HttpHeaders.ACCEPT, "*/*");
    return Lists.newArrayList(authorization, accept);
  }

  public File resignUsingFiles(ProvisioningProfile profile, String name, URL ipaUrl)
    throws TestsigmaException {
    File ipa = new File(ThreadContext.get("X-Request-Id") + "_" + name + ".ipa");
    try {
      certificateService.setPreSignedURLs(profile);
      CloseableHttpClient httpClient = HttpClients.custom().build();
      List<Header> headers = getResignHeaders();
      String url = testsigmaOSConfigService.getUrl() + ISIGN_USING_FILES_URL;
      log.info(String.format("Sending a resign request for file %s to server url %s", name, url));
      HttpPut putRequest = new HttpPut(url);
      putRequest.setHeaders(headers.toArray(Header[]::new));
      MultipartEntityBuilder builder = MultipartEntityBuilder.create();
      builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
      builder.addPart("certificate", copyUrlToFile(profile.getCertificateCrtPresignedUrl()));
      builder.addPart("privateKey", copyUrlToFile(profile.getPrivateKeyPresignedUrl()));
      builder.addPart("provisioningProfile", copyUrlToFile(profile.getProvisioningProfilePresignedUrl()));
      builder.addPart("ipa", copyUrlToFile(ipaUrl));
      builder.addPart("name", new StringBody(name, ContentType.MULTIPART_FORM_DATA));
      putRequest.setEntity(builder.build());
      org.apache.http.HttpResponse res = httpClient.execute(putRequest);

      Integer status = res.getStatusLine().getStatusCode();
      log.info("Resign Using Files Service Response - " + status);
      if (status.equals(HttpStatus.OK.value())) {
        BufferedInputStream bis = new BufferedInputStream(res.getEntity().getContent());
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(ipa));
        int inByte;
        while ((inByte = bis.read()) != -1) bos.write(inByte);
        bis.close();
        bos.close();
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new TestsigmaException(e);
    }
    return ipa;
  }

  public void resignUsingUrls(ProvisioningProfile profile, String name, URL ipaUrl, URL resignedIpaUrl)
    throws TestsigmaException {
    certificateService.setPreSignedURLs(profile);

    ResignRequestUsingUrlsDTO resignRequestUsingUrlsDTO = new ResignRequestUsingUrlsDTO();
    resignRequestUsingUrlsDTO.setCertificate(profile.getCertificateCrtPresignedUrl());
    resignRequestUsingUrlsDTO.setPrivateKey(profile.getPrivateKeyPresignedUrl());
    resignRequestUsingUrlsDTO.setProvisioningProfile(profile.getProvisioningProfilePresignedUrl());
    resignRequestUsingUrlsDTO.setIpa(ipaUrl);
    resignRequestUsingUrlsDTO.setResignedIpa(resignedIpaUrl);
    resignRequestUsingUrlsDTO.setName(name);

    HttpResponse<String> response = httpClient.put(testsigmaOSConfigService.getUrl()
        + ISIGN_USING_URLS_URL,
      getResignHeaders(),
      resignRequestUsingUrlsDTO, new TypeReference<>() {
      });

    log.info("Resign Service Response - " + response);
    if (response.getStatusCode() >= 300) {
      throw new TestsigmaException(String.format("Exception while re-signing the " + name + " ipa file [%s] - [%s]"
        , response.getStatusCode(), response.getStatusMessage()));
    }
  }

  private FileBody copyUrlToFile(URL url) throws IOException {
    String fileName = getFileName(url);
    File localFile = Paths.get(System.getProperty("java.io.tmpdir"), fileName).toFile();
    FileUtils.copyURLToFile(url, localFile, (60 * 1000), (60 * 1000));
    return new FileBody(localFile, ContentType.DEFAULT_BINARY);
  }

  private String getFileName(URL url) {
    String fileName = FilenameUtils.getName(url.getPath());
    if (fileName.indexOf("?") > 0) {
      fileName = fileName.substring(0, fileName.indexOf("?"));
    }

    return fileName;
  }
}
