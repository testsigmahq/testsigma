/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.mapper;

import com.testsigma.dto.api.APIUploadDTO;
import com.testsigma.dto.UploadDTO;
import com.testsigma.dto.export.UploadXMLDTO;
import com.testsigma.model.Upload;
import com.testsigma.service.UploadService;
import org.apache.commons.io.FileUtils;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)

public interface UploadMapper {
  UploadXMLDTO mapTo(Upload upload);

  default List<UploadXMLDTO> mapUploads(List<Upload> uploads, UploadService uploadService, File srcFiles) {
    List<UploadXMLDTO> list = new ArrayList<>();
    try {
      String uploadFolderName = "upload";
      File uploadFolder = new File(srcFiles.getAbsolutePath() + File.separator + uploadFolderName);
      if (!uploadFolder.exists()) {
        uploadFolder.mkdir();
      }
      for (Upload upload : uploads) {
        try {
          list.add(mapTo(upload));
          FileUtils.copyURLToFile(new URL(uploadService.getPreSignedURL(upload)),
            new File(uploadFolder.getAbsolutePath() + File.separator + upload.getFileName()));
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return list;
  }

  UploadDTO map(Upload upload);
  APIUploadDTO mapApi(Upload upload);

  List<UploadDTO> map(List<Upload> uploads);
  List<APIUploadDTO> mapApis(List<Upload> uploads);

}
