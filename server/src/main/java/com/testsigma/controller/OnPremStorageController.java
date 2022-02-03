package com.testsigma.controller;

import com.testsigma.config.StorageServiceFactory;
import com.testsigma.config.URLConstants;
import com.testsigma.model.PreSignedAttachmentToken;
import com.testsigma.service.JWTTokenService;
import com.testsigma.service.OnPremiseStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping(path = URLConstants.PRESIGNED_BASE_URL)
@Log4j2
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class OnPremStorageController {
  private final StorageServiceFactory storageServiceFactory;
  private final JWTTokenService jwtTokenService;

  @PostMapping(value = "/**")
  @ResponseStatus(HttpStatus.CREATED)
  public void create(HttpServletRequest request, @NotNull @ModelAttribute MultipartFile file) throws IOException {
    PreSignedAttachmentToken preSignedAttachmentToken = jwtTokenService.parseAttachmentToken(request.getParameter(OnPremiseStorageService.STORAGE_SIGNATURE));
    storageServiceFactory.getStorageService().addFile(preSignedAttachmentToken.getKey(), new ByteArrayInputStream(file.getBytes()));
  }

  @GetMapping(value = "/**")
  public void show(HttpServletRequest request, HttpServletResponse response) throws IOException {
    PreSignedAttachmentToken preSignedAttachmentToken = jwtTokenService.parseAttachmentToken(request.getParameter(OnPremiseStorageService.STORAGE_SIGNATURE));
    String filePath = ((OnPremiseStorageService) storageServiceFactory.getStorageService()).getAbsoluteFilePath(preSignedAttachmentToken.getKey());
    response.setHeader("Content-Disposition", "attachment; filename=" + new File(filePath).getName());
    response.getOutputStream().write(storageServiceFactory.getStorageService().getFileByteArray(filePath));
  }

  @DeleteMapping(value = "/**")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void delete(HttpServletRequest request) {
    PreSignedAttachmentToken preSignedAttachmentToken = jwtTokenService.parseAttachmentToken(request.getParameter(OnPremiseStorageService.STORAGE_SIGNATURE));
    String filePath = ((OnPremiseStorageService) storageServiceFactory.getStorageService()).getAbsoluteFilePath(preSignedAttachmentToken.getKey());
    storageServiceFactory.getStorageService().deleteFile(filePath);
  }

}
