package com.testsigma.web.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class YamlImportRequest {
    MultipartFile multipartFile;
}
