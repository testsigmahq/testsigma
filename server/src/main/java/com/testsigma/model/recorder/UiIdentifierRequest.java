package com.testsigma.model.recorder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.testsigma.dto.ElementScreenNameDTO;
import com.testsigma.model.*;
import com.testsigma.web.request.ElementRequest;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Log4j2
public class UiIdentifierRequest {
    private Long id;
    @NotEmpty
    private String name;
    @NotNull
    private ElementCreateType createdType;
    @NotNull
    private LocatorType locatorType;
    private Boolean isAdvanced;
    private String definition;
    private String attributes;
    private ElementMetaDataRequest metadata;
    private Boolean isDynamic = Boolean.FALSE;
    @NotNull
    private Long applicationVersionId;
    private String comments;
    //private UiIdentifierStatus status = UiIdentifierStatus.READY;
    private Timestamp reviewSubmittedAt;
    private Timestamp draftAt;
    private Long draftBy;
    private Long reviewSubmittedBy;
    private Long reviewedBy;
    private Timestamp reviewedAt;
    private Boolean sendMailNotification;
    private Long screenNameId;
    private String screenShot;
    private String screenShotData;
    private Boolean isShadowDom = Boolean.FALSE;
    private String tag;
    private Map<String, Object> attrs;
    private ElementRequest[] siblings;
    private ElementRequest[] parents;

    @JsonIgnore
    private ElementScreenNameDTO screenNameObj;
    private String currentImgBase64;
    private String currentElementSource;
    private Long shadowParentId;
    private List<UiIdentifierRequest> shadowParentElements = new ArrayList<>();
    private Long shadowOrder;

    /*public boolean isDraft() {
        return this.status.equals(UiIdentifierStatus.DRAFT);
    }

    public boolean isInReview() {
        return this.status.equals(UiIdentifierStatus.IN_REVIEW);
    }

    public boolean isReady() {
        return this.status.equals(UiIdentifierStatus.READY);
    }*/
}

