package com.testsigma.model.recorder;

import com.testsigma.dto.ElementMetaDataDTO;
import com.testsigma.dto.ElementScreenNameDTO;
import com.testsigma.model.ElementCreateType;
import com.testsigma.model.LocatorType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode
public class UiIdentifierDTO {
    private Long id;
    private Long applicationVersionId;
    private String definition;
    private String name;
    @ToString.Exclude
    private String fieldName;
    private Integer type;
    private ElementCreateType createdType;
    private LocatorType locatorType;
    private Boolean isAdvanced;
    private ElementMetaDataDTO metadata;
    private String attributes;
    private Boolean autoHealingEnabled;
    private Boolean isDynamic;
    private Timestamp createdDate;
    private Timestamp updatedDate;
    private Long screenNameId;
    private UiIdentifierScreenNameDTO screenNameObj;
    private Boolean isDuplicated;
    private String screenShotURL;
    private String screenShotData;
    private String elementSourceUrl;
    private String screenShot;
    private Boolean isShadowDom;
    private String tag;
    private Long shadowParentId;
    private List<UiIdentifierDTO> shadowParentElements;
    private Long shadowOrder;

}

