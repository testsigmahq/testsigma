/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.mapper;

import com.testsigma.dto.BackupDTO;
import com.testsigma.dto.UploadVersionDTO;
import com.testsigma.dto.api.APIUploadDTO;
import com.testsigma.dto.UploadDTO;
import com.testsigma.dto.export.UploadCloudXMLDTO;
import com.testsigma.dto.export.UploadVersionXMLDTO;
import com.testsigma.dto.export.UploadXMLDTO;
import com.testsigma.model.Upload;
import com.testsigma.model.UploadVersion;
import com.testsigma.service.UploadService;
import com.testsigma.service.UploadVersionService;
import com.testsigma.web.request.UploadRequest;
import org.apache.commons.io.FileUtils;
import org.mapstruct.*;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)

public interface UploadMapper {
    UploadXMLDTO mapTo(Upload upload);

    Upload copy(Upload upload);

    UploadVersion copyVersion(UploadVersion upload);

    UploadVersionXMLDTO mapVersionTo(UploadVersion version);

    List<UploadXMLDTO> mapUploads(List<Upload> uploads);

    UploadDTO map(Upload upload);

    List<UploadDTO> map(List<Upload> uploads);

    Upload map(UploadRequest uploadRequest);

    void merge(UploadRequest uploadRequest, @MappingTarget Upload upload);

    Upload mapTo(UploadXMLDTO upload);


    default List<Upload> mapUploadsList(List<UploadXMLDTO> uploads, UploadService uploadService, BackupDTO importDTO) {
        List<Upload> list = new ArrayList<>();
        try {

            for (UploadXMLDTO uploadXMLDTO : uploads) {
                try {
                    Upload upload = mapTo(uploadXMLDTO);
                    list.add(upload);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    default List<UploadVersionXMLDTO> mapUploads(List<UploadVersion> versions, UploadVersionService versionService, File srcFiles) {
        List<UploadVersionXMLDTO> list = new ArrayList<>();
        try {
            String uploadFolderName = "upload";
            File uploadFolder = new File(srcFiles.getAbsolutePath() + File.separator + uploadFolderName);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdir();
            }
            for (UploadVersion version : versions) {
                try {
                    UploadVersionXMLDTO xmldto = mapVersionTo(version);
                    FileUtils.copyURLToFile(new URL(versionService.getPreSignedURL(version)), new File(uploadFolder.getAbsolutePath() + File.separator + version.getFileName()));
                    xmldto.setDownloadURL(versionService.getPreSignedURL(version));
                    list.add(xmldto);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    APIUploadDTO mapApi(Upload upload);

    List<APIUploadDTO> mapApis(List<Upload> uploads);

    List<UploadVersionDTO> mapVersions(List<UploadVersion> versions);

    UploadVersionDTO mapVersion(UploadVersion version);

    List<Upload> mapUploadsXMLList(List<UploadXMLDTO> readValue);

    List<Upload> mapUploadsCloudXMLList(List<UploadCloudXMLDTO> readValue);

    List<UploadVersion> mapUploadVersionsList(List<UploadVersionXMLDTO> uploads);
}
