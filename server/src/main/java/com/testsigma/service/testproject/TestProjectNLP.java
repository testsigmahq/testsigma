package com.testsigma.service.testproject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testsigma.exception.TestProjectImportException;
import com.testsigma.model.WorkspaceType;
import com.testsigma.service.ObjectMapperService;
import com.testsigma.web.request.testproject.TestProjectNLPTemplate;
import lombok.extern.log4j.Log4j2;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@Log4j2
public class TestProjectNLP {

    private static String ANDROID_ACTIONS_PATH = "testproject/android_actions.json";
    private static String WEB_ACTIONS_PATH = "testproject/web_actions.json";
    private static String IOS_ACTIONS_PATH  = "testproject/ios_actions.json";

    private static List<TestProjectNLPTemplate> webActions;
    private static List<TestProjectNLPTemplate> androidActions;
    private static List<TestProjectNLPTemplate> iosActions;


    public TestProjectNLP() {
        webActions = readActions(WEB_ACTIONS_PATH);
        androidActions = readActions(ANDROID_ACTIONS_PATH);
        iosActions = readActions(IOS_ACTIONS_PATH);
    }

    public TestProjectNLPTemplate getNlpByIdAndType(String id, WorkspaceType type) throws TestProjectImportException {
        List<TestProjectNLPTemplate> nlpList = getActionsByType(type);
        for (TestProjectNLPTemplate template : nlpList) {
            if (template.getGuid().equals(id))
                return template;
        }
        return null;
    }

    private List<TestProjectNLPTemplate> getActionsByType(WorkspaceType type) throws TestProjectImportException {
        switch (type){
            case WebApplication:
                return webActions;
            case AndroidNative:
                return androidActions;
            case IOSNative:
                return iosActions;
            default:
                throw new TestProjectImportException("ApplicationType not supported - " + type);
        }
    }


    private List<TestProjectNLPTemplate> readActions(String path){
        BufferedReader toReturn = null;
        try {
            InputStream is = TestProjectNLP.class.getClassLoader().getResourceAsStream(path);
            toReturn = new BufferedReader(new InputStreamReader(is));
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        }
        return new ObjectMapperService().parseJson(toReturn, new TypeReference<>() {
        });
    }

}
