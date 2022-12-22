package com.testsigma.web.request;

import lombok.Data;

@Data
public class ExternalImportRequest {
    private String importType;
    private String gitRepoUrl;
    private String gitToken;

    public boolean isYamlImport(){
        return importType != null && importType.equalsIgnoreCase("YAML");
    }

    public boolean isGithubImport(){
        return importType != null && importType.equalsIgnoreCase("GIT");
    }

}
