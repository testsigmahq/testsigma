package com.testsigma.web.request.testproject;

import com.testsigma.model.LocatorType;
import lombok.Data;
import org.springframework.security.core.parameters.P;
import org.xml.sax.Locator;

import java.rmi.registry.LocateRegistry;

@Data
public class TestProjectElementLocatorRequest {
    private String name;
    private String value;
    private Long priority;

    public LocatorType getLocatorType(){
        switch (name) {
            case "CSSSELECTOR":
                return LocatorType.csspath;
            case "XPATH":
                return LocatorType.xpath;
            case "ACCESSIBILITYID":
                return LocatorType.accessibility_id;
            case "TAGNAME":
                return LocatorType.tag_name;
            case  "ID" :
                return LocatorType.id_value;
            case "CLASSNAME":
                return LocatorType.class_name;
            default:
                return null;
        }
    }
}
