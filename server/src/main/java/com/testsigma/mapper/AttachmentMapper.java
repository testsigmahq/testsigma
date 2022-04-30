/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.mapper;

import com.testsigma.dto.AttachmentDTO;
import com.testsigma.dto.export.AttachmentCloudXMLDTO;
import com.testsigma.dto.export.AttachmentXMLDTO;
import com.testsigma.model.Attachment;
import com.testsigma.service.AttachmentService;
import com.testsigma.web.request.AttachmentRequest;
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
public interface AttachmentMapper {

  Attachment map(AttachmentRequest attachmentRequest);

  AttachmentDTO mapToDTO(Attachment attachment);

  List<AttachmentDTO> mapToDTO(List<Attachment> attachments);

  AttachmentXMLDTO map(Attachment upload);

  default List<AttachmentXMLDTO> mapAttachments(List<Attachment> attchements, AttachmentService attachmentService, File srcFiles) {
    List<AttachmentXMLDTO> list = new ArrayList<>();
    try {
      String attachments = "attachments";
      File uploadFolder = new File(srcFiles.getAbsolutePath() + File.separator + attachments);
      if (!uploadFolder.exists()) {
        uploadFolder.mkdir();
      }
      for (Attachment attachment : attchements) {
        try {
          list.add(map(attachment));
          attachmentService.getPreSignedURL(attachment);
          FileUtils.copyURLToFile(new URL(attachment.getPreSignedURL()),
            new File(uploadFolder.getAbsolutePath() + File.separator + attachment.getName()));
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return list;
  }

  List<Attachment> mapCloudAttachmentsList(List<AttachmentCloudXMLDTO> readValue);
  List<Attachment> mapAttachmentsList(List<AttachmentXMLDTO> readValue);

  Attachment copy(Attachment attachment);
}
