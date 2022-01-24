package com.testsigma.dto;

import com.testsigma.model.ProvisioningProfileStatus;
import lombok.Data;

import java.net.URL;
import java.sql.Timestamp;
import java.util.List;

@Data
public class ProvisioningProfileDTO {
  private Long id;
  private String name;
  private String teamId;
  private ProvisioningProfileStatus status;
  private URL csrPresignedUrl;
  private URL privateKeyPresignedUrl;
  private URL certificateCerPresignedUrl;
  private URL certificateCrtPresignedUrl;
  private URL certificatePemPresignedUrl;
  private URL provisioningProfilePresignedUrl;
  private Timestamp createdDate;
  private Timestamp updatedDate;
  private List<String> deviceUDIDs;
}
