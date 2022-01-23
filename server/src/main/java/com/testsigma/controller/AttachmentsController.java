/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.controller;

import com.testsigma.dto.AttachmentDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.AttachmentMapper;
import com.testsigma.model.Attachment;
import com.testsigma.service.AttachmentService;
import com.testsigma.specification.AttachmentSpecificationsBuilder;
import com.testsigma.web.request.AttachmentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping(path = "/attachments")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AttachmentsController {
  private final AttachmentService attachmentService;
  private final AttachmentMapper attachmentMapper;

  @RequestMapping(method = RequestMethod.POST)
  public AttachmentDTO create(@ModelAttribute @Valid AttachmentRequest attachmentRequest) throws IOException {
    Attachment attachment = this.attachmentService.create(attachmentRequest);
    return attachmentMapper.mapToDTO(attachment);
  }

  @RequestMapping(method = RequestMethod.GET)
  public Page<AttachmentDTO> index(AttachmentSpecificationsBuilder builder, Pageable pageable) {
    Specification<Attachment> spec = builder.build();
    Page<Attachment> attachments = this.attachmentService.findAll(spec, pageable);
    List<AttachmentDTO> attachmentDTOS =
      attachmentMapper.mapToDTO(attachments.getContent());
    return new PageImpl<>(attachmentDTOS, pageable, attachments.getTotalElements());
  }

  @GetMapping(path = "/{id}")
  public AttachmentDTO show(@PathVariable("id") Long id) throws ResourceNotFoundException {
    Attachment attachment = this.attachmentService.find(id);
    return attachmentMapper.mapToDTO(attachment);
  }

  @GetMapping(path = "/{id}/preview")
  @ResponseStatus(code = HttpStatus.MOVED_TEMPORARILY)
  public void preview(@PathVariable("id") Long id, HttpServletResponse httpServletResponse) throws ResourceNotFoundException {
    Attachment attachment = this.attachmentService.find(id);
    httpServletResponse.setHeader("Location", attachment.getPreSignedURL());
    httpServletResponse.setStatus(HttpStatus.MOVED_TEMPORARILY.value());
  }

  @DeleteMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void delete(@PathVariable("id") Long id) throws ResourceNotFoundException {
    this.attachmentService.destroy(id);
  }

}
