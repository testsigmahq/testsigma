package com.testsigma.dto;

import lombok.Data;

import java.net.URL;

@Data
public class ResignRequestUsingUrlsDTO {
  URL certificate;
  URL privateKey;
  URL provisioningProfile;
  URL ipa;
  URL resignedIpa;
  String name;
}
