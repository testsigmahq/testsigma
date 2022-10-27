package com.testsigma.model.recorder;

import com.testsigma.model.WorkspaceType;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class ApplicationDTO {
    private Long id;
    private String name;
    private String description;
    private String customFields;
    //private Long projectId;
    private WorkspaceType applicationType;
    //private ProjectDTO project;
    private Long createdById;
    private Long updatedById;
    private Timestamp createdDate;
    private Timestamp updatedDate;
}
