package com.testsigma.model;

import com.testsigma.model.ExternalImportType;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "external_import_nlp_mappings")
@Data
public class ExternalImportNlpMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "workspace_type")
    @Enumerated(EnumType.STRING)
    private WorkspaceType workspaceType;

    @Column(name = "testsigma_nlp_id")
    private Integer testsigmaNlpId;

    @Column(name = "external_nlp_id")
    private String externalNlpId;

    @Column(name = "external_import_type")
    @Enumerated(EnumType.STRING)
    private ExternalImportType externalImportType;

}
