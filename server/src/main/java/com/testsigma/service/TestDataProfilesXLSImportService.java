package com.testsigma.service;

import com.testsigma.exception.TestsigmaValidationException;
import com.testsigma.model.StorageAccessLevel;
import com.testsigma.util.ReadExcel;
import com.testsigma.util.XLSUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.ListUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestDataProfilesXLSImportService extends XLSImportService {

    private final TestDataImportService testDataImportService;

    public void importFile(String name, List<String> passwords, MultipartFile testDataFile, Long versionId, boolean isReplace) throws Exception {
        downloadAndProcessTestData(testDataFile, name, versionId, passwords, !isReplace);
    }

    public void downloadAndProcessTestData(MultipartFile testDataFile, String name, Long versionId, List<String> passwords, boolean canIgnore) throws Exception {
        Map<String, Integer> testDataProfileColumnNameIndexMap;
        Workbook workBook = new XSSFWorkbook(testDataFile.getInputStream());
        Collection<List<Object>> columnNames = ReadExcel.getExelFieldNames(workBook).values();
        try {
            testDataProfileColumnNameIndexMap = TestDataImportService.getFirstSheetFieldIdMap(TestDataImportService.getFiledNames(), columnNames);
        } catch (TestsigmaValidationException e) {
            log.error(e.getMessage(), e);
            incorrectColumnErrors(Arrays.asList(e.getMessage().split(",")));
            return;
        }
        List<List<List<Object>>> sheetsDataRows = getExelDataList(workBook);
        List<String> defaultColumnNames = TestDataImportService.getFiledNames();

        if (sheetsDataRows.size() > 0) {
            log.debug("Processing uploaded test data");
            processTestData(sheetsDataRows, defaultColumnNames, testDataProfileColumnNameIndexMap, Arrays.asList(columnNames.toArray()), name, versionId, passwords, canIgnore);
            log.debug("Added test data to DB after processing");
        }
    }

    public void processTestData(List<List<List<Object>>> sheetsDataList, List<String> fieldNames, Map<String,
            Integer> nameIndexMap, List<Object> columnNames, String name, Long versionId,
                                List<String> passwords, boolean canIgnore) throws Exception {
        testDataImportService.initializeEmptyObjects();
        List<List<Object>> errors = new ArrayList<>();
        Integer size = 0;
        try {
            List<List<Object>> firstSheetDataRowsList = sheetsDataList.get(0);
            size = firstSheetDataRowsList.size();
            for (int i = 0; i < firstSheetDataRowsList.size(); i++) {
                List<Object> currentRowDataList = firstSheetDataRowsList.get(i);
                Object errorObject = testDataImportService.getRowObjects(nameIndexMap, currentRowDataList, columnNames);
                if (errorObject instanceof List) {
                    ArrayList<Object> currentRowErrorObjectList = (ArrayList<Object>) errorObject;
                    if (currentRowErrorObjectList.size() > 0) {
                        errors.add(ListUtils.union(currentRowDataList, currentRowErrorObjectList));
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            List<Object> row = new ArrayList<Object>();
            row.add(e.getMessage());
            errors.add(row);
        }
        XLSUtil wrapper = new XLSUtil();
        try {
            if (errors.size() == size) {
                saveErrors(fieldNames, columnNames, nameIndexMap, errors, size);
            } else if (errors.size() > 0) {
                saveErrors(fieldNames, columnNames, nameIndexMap, errors, size);
                testDataImportService.addTestDataToDB(name,columnNames, versionId, passwords, canIgnore);
            } else {
                wrapper.setStorageService(super.getStorageServiceFactory().getStorageService());
                testDataImportService.addTestDataToDB(name,columnNames, versionId, passwords, canIgnore);
                log.info("Test data import successful");
            }
        } catch (Exception e) {
            log.info("Test data import failed");
            log.error(e.getMessage(), e);
        }
    }

    private void saveErrors(List<String> filedNames, List<Object> columnNames, Map<String, Integer> nameIndexMap, List<List<Object>> rows,
                            Integer total) throws Exception {
        List<String> columnNamesList = columnNames.stream()
                .map(o -> {
                    List<String> tempList = new ArrayList<>((List<String>) o);
                    return tempList;
                })
                .collect(Collectors.toList()).get(0);

        XLSUtil wrapper = new XLSUtil();
        wrapper.setStorageService(super.getStorageServiceFactory().getStorageService());
        Row headerRow = wrapper.getHeaderRow();
        CellStyle headerStyle = XLSUtil.getTableHeaderStyle(wrapper);
        for (int i = 0; i < columnNamesList.size(); i++) {
            XLSUtil.createColumn(headerRow, i, columnNamesList.get(i), headerStyle);
        }
        XLSUtil.createColumn(headerRow, columnNamesList.size(), "Errors", headerStyle);

        CellStyle dataStyle = XLSUtil.getAlignStyle(wrapper);

        Row dataRow;
        for (int i = 0; i < rows.size(); i++) {
            dataRow = wrapper.getDataRow(wrapper, i + 1);
            List<Object> row = rows.get(i);
            int j = 0;
            for (; j < filedNames.size(); j++) {
                XLSUtil.createColumn(dataRow, j, row.get(nameIndexMap.get(filedNames.get(j))), dataStyle);
            }
            for (; j < row.size(); j++) {
                XLSUtil.createColumn(dataRow, j, row.get(j), dataStyle);
            }
        }
        if(total.equals(rows.size())) {
            log.info("Test Data import successful");
        } else {
            log.info("Test Data import partially successful");
        }
    }

    @Override
    public Object cellToObject(Cell cell, boolean testData) {
        return new DataFormatter().formatCellValue(cell);
    }
}

