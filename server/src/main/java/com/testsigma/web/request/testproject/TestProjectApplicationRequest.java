package com.testsigma.web.request.testproject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.testsigma.model.WorkspaceType;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestProjectApplicationRequest {
    private String id;
    private String name;
    private String platform;


    public WorkspaceType getPlatform(){
        switch (this.platform){
            case "Web":
                return WorkspaceType.WebApplication;
            case "Android":
                return WorkspaceType.AndroidNative;
            case "iOS":
                return WorkspaceType.IOSNative;
        }
        return null;
    }

}
