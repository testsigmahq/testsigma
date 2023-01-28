package com.testsigma.dto;


import com.testsigma.model.ReportModule;
import com.testsigma.model.ReportType;
import com.testsigma.model.WorkspaceVersion;
import lombok.Data;

@Data
public class ReportsDTO {
    private Long id;
    private String name;
    private String description;
    private ReportType reportType;
    private ReportModule reportModule;
    private WorkspaceVersion workspaceVersion;
}

