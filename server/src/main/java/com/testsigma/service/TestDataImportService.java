package com.testsigma.service;

import com.testsigma.constants.MessageConstants;
import com.testsigma.exception.ExceptionErrorCodes;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestsigmaValidationException;
import com.testsigma.model.TestData;
import com.testsigma.model.TestDataSet;
import com.testsigma.util.RowAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired, @Lazy}))
public class TestDataImportService implements RowAdapter {
    private final TestDataProfileService service;

    public static final String TEST_DATA_NAME = "name";
    public static final String TEST_DATA_DESCRIPTION = "description";
    public static final String TEST_DATA_EXPECTED_TO_FAIL = "expectedtofail";

    private static final List<String> filedNames = new ArrayList<String>();

    static {
        filedNames.add(TEST_DATA_NAME);
        filedNames.add(TEST_DATA_DESCRIPTION);
        filedNames.add(TEST_DATA_EXPECTED_TO_FAIL);
    }

    private ArrayList<TestDataSet> testDataSetList = new ArrayList<>();
    private TestData testData = new TestData();
    private final ArrayList<Object> rowObjects = new ArrayList<>();


    public static List<String> getFiledNames() {
        return filedNames;
    }

    public static Map<String, Integer> getFirstSheetFieldIdMap(List<String> fieldNames, Collection<List<Object>> columnNameList) throws TestsigmaValidationException {
        Map<String, Integer> nameIndexMap = new HashMap<>();
        for (List<Object> columnNames : columnNameList) {
            for (int j = 0; j < fieldNames.size(); j++) {
                String fieldName = fieldNames.get(j).toLowerCase().trim();
                for (int i = 0; i < columnNames.size(); i++) {
                    String column = columnNames.get(i).toString().toLowerCase().trim();
                    if ((column != null) && fieldName.equals(column))
                        nameIndexMap.put(fieldName, i);
                }
            }
            break;
        }
        if (nameIndexMap.size() != fieldNames.size()) {
            String message = "";
            for (int i = 0; i < fieldNames.size(); i++) {
                String fieldname = fieldNames.get(i);
                Integer index = nameIndexMap.get(fieldname);
                if (index == null) {
                    message = fieldname + "," + message;
                }
            }
            message = StringUtils.substring(message, 0, message.length() - 1);
            throw new TestsigmaValidationException(ExceptionErrorCodes.FIELD_DEFINITION_COLUMONS_NOT_FOUND, message);
        }
        return nameIndexMap;
    }

    @Override
    public Object[] getRowObjects(Map<String, Integer> nameIndexMap, List<Object> fieldValueArray, Object[] commonValues, int i) throws Exception {
        rowObjects.add(getRowObjects(nameIndexMap, fieldValueArray, new ArrayList<Object>(Arrays.asList("")), commonValues));
        return rowObjects.toArray();
    }

    public void initializeEmptyObjects(){
        testData = new TestData();
        testDataSetList = new ArrayList<>();
    }

    public Object getRowObjects(Map<String, Integer> defaultColumnNameIndexMap, List<Object> currentRowDataList, List<Object> columnNames) throws TestsigmaValidationException {
        Collection<Integer> indexes = defaultColumnNameIndexMap.values();
        String name = currentRowDataList.get(defaultColumnNameIndexMap.get(filedNames.get(0))).toString();
        String description = currentRowDataList.get(defaultColumnNameIndexMap.get(filedNames.get(1))).toString();
        Boolean expectedToFail = (currentRowDataList.get(defaultColumnNameIndexMap.get(filedNames.get(2)))).toString().equalsIgnoreCase("yes");
        List<String> errors = new ArrayList<>();
        List<String> columnNamesList = columnNames.stream()
                .map(o -> {
                    List<String> tempList = new ArrayList<>((List<String>) o);
                    return tempList;
                })
                .collect(Collectors.toList()).get(0);

        LinkedHashMap<String, String> rowMapdata = new LinkedHashMap<String, String>();
        for (int k = 0; k < currentRowDataList.size(); k++) {
            boolean isNotVisited = true;
            for (int index = 0; index < indexes.size(); index++) {
                if (index == k)
                    isNotVisited = false;
            }
            if (isNotVisited) {
                Object obj = currentRowDataList.get(k);
                rowMapdata.put(columnNamesList.get(k), obj.toString());
            }
        }
        TestDataSet currentTestDataSet = createTestData(name, description, expectedToFail, rowMapdata);
        List<String> validationErrors = validateTestDataSet(name, expectedToFail, rowMapdata);
        if (validationErrors.size() > 0) errors.addAll(validationErrors);
        else {
            testDataSetList.add(currentTestDataSet);
        }
        return errors;
    }

    @Override
    public Object getRowObjects(Map<String, Integer> nameIndexMap, List<Object> fieldValueArray, List<Object> columnNames, Object[] commonValues) throws Exception {
        return null;
    }

    private List<String> validateTestDataSet(String setName, Boolean expectedToFail, LinkedHashMap<String, String> rowMapData) {
        List<String> errors = new ArrayList<>();

        String invalidCharacterPresentErrorString = checkInvalidCharacters(rowMapData);
        if (!invalidCharacterPresentErrorString.isEmpty())
            errors.add(invalidCharacterPresentErrorString);


        if (setName.isEmpty())
            errors.add("Set name is empty.");
        else if(testDataSetList.stream().anyMatch((data) -> data.getName().equals(setName)))
            errors.add("Set name is duplicate.");
        if(expectedToFail == null)
            errors.add("expectedToFail is empty.");
        return errors;
    }

    public TestDataSet createTestData(String name, String description, Boolean expectedToFail, LinkedHashMap<String, String> rowMapdata) {
        TestDataSet testDataSet = new TestDataSet();
        testDataSet.setName(name);
        testDataSet.setDescription(description);
        testDataSet.setExpectedToFail(expectedToFail);
        testDataSet.setData(getOrderedJSONObject(rowMapdata));
        return testDataSet;
    }

    private JSONObject getOrderedJSONObject(LinkedHashMap<String, String> orderedMap) {
        JSONObject orderedJSONObject = new JSONObject();
        Field changeMaptoLinkedHashMapReflectionField;
        try {
            changeMaptoLinkedHashMapReflectionField = orderedJSONObject.getClass().getDeclaredField("map");
            changeMaptoLinkedHashMapReflectionField.setAccessible(true);
            changeMaptoLinkedHashMapReflectionField.set(orderedJSONObject, new LinkedHashMap<>());
            changeMaptoLinkedHashMapReflectionField.setAccessible(false);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            log.info(e.getMessage());
        }
        orderedMap.forEach(orderedJSONObject::put);
        return orderedJSONObject;
    }

    public void addTestDataToDB(String name,List<Object> columnNames, Long versionId, List<String> passwords, boolean canIgnore) {
        Long id = null;
        try {
            id = service.findByTestDataNameAndVersionId(name, versionId).getId();
        } catch (ResourceNotFoundException e) {
            log.debug("Adding new test data profile");
        }
        List<String> columns = convertColumnTypeToString(columnNames.get(0));
        testData.setTestDataName(name);
        testData.setVersionId(versionId);
        testData.setTempTestData(testDataSetList);
        if (passwords.size() > 0) {
            testData.setPasswords(passwords);
            testData = service.encryptPasswords(testData);
        }
        if(id == null) {
            service.create(testData);
        } else if(canIgnore) {
            log.info("Entity already present and Skip on duplicate is selected. So ignoring import for Test Data Profile = " + name);
        } else {
            try {
                // Since it's Replace doing Delete and Create instead of update.
                service.destroy(id);
                service.create(testData);
            } catch (ResourceNotFoundException e) {
                log.debug("Failed to update service");
            }
        }
    }

    private List<String> convertColumnTypeToString(Object column){
        List<String> columnStrings = (List) column;
        return columnStrings.subList(3,columnStrings.size());
    }

    private String checkInvalidCharacters(LinkedHashMap<String, String> rowMapData) {
        StringBuilder invalidCharacterErrorBuilder = new StringBuilder();

        for (Map.Entry<String, String> columnNameValueMap : rowMapData.entrySet()) {
            int indexOfNewlineCharacterInParameterName = columnNameValueMap.getKey().indexOf("\n");
            int indexOfBackslashCharacterInParameterName = columnNameValueMap.getKey().indexOf("\\");
            if (indexOfNewlineCharacterInParameterName != -1)
                generateErrorMessageForInvalidParameterName(invalidCharacterErrorBuilder, indexOfNewlineCharacterInParameterName, columnNameValueMap);
            if (indexOfBackslashCharacterInParameterName != -1)
                generateErrorMessageForInvalidParameterName(invalidCharacterErrorBuilder, indexOfBackslashCharacterInParameterName, columnNameValueMap);


            int indexOfNewlineCharacterInParameterSet = columnNameValueMap.getValue().indexOf("\n");
            int indexOfBackslashCharacterInParameterSet = columnNameValueMap.getValue().indexOf("\\");
            if (indexOfNewlineCharacterInParameterSet != -1)
                generateErrorMessageForInvalidParameterSet(invalidCharacterErrorBuilder, indexOfNewlineCharacterInParameterSet, columnNameValueMap);
            if (indexOfBackslashCharacterInParameterSet != -1)
                generateErrorMessageForInvalidParameterSet(invalidCharacterErrorBuilder, indexOfBackslashCharacterInParameterSet, columnNameValueMap);
        }
        if (invalidCharacterErrorBuilder.length() > 0) {
            invalidCharacterErrorBuilder.append(". ").append(MessageConstants.INVALID_TEST_DATA_SET_TRY_AGAIN);
        }

        return invalidCharacterErrorBuilder.toString();
    }

    void generateErrorMessageForInvalidParameterName(StringBuilder invalidCharacterErrorBuilder, int indexOfError, Map.Entry<String, String> columnNameValueMap) {
        if (invalidCharacterErrorBuilder.length() > 0) invalidCharacterErrorBuilder.append(", ");
        invalidCharacterErrorBuilder
                .append(String.format(MessageConstants.INVALID_TEST_DATA_PARAMETER_SPECIAL_CHARACTER, indexOfError))
                .append("'").append(columnNameValueMap.getKey()).append("'");
    }

    void generateErrorMessageForInvalidParameterSet(StringBuilder invalidCharacterErrorBuilder, int indexOfError, Map.Entry<String, String> columnNameValueMap) {
        if (invalidCharacterErrorBuilder.length() > 0) invalidCharacterErrorBuilder.append(", ");
        invalidCharacterErrorBuilder
                .append(String.format(MessageConstants.INVALID_TEST_DATA_SET_SPECIAL_CHARACTER, indexOfError))
                .append("'").append(columnNameValueMap.getKey()).append("'");
    }
}

