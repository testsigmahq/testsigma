package com.testsigma.service;

import com.testsigma.config.StorageServiceFactory;
import com.testsigma.exception.ExceptionErrorCodes;
import com.testsigma.exception.TestsigmaValidationException;
import com.testsigma.util.XLSUtil;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Log4j2
public abstract class XLSImportService {

    @Getter
    @Autowired
    private StorageServiceFactory storageServiceFactory;

    public List<List<List<Object>>> getExelDataList(
            Workbook testDataWorkBook) throws Exception {
        String errormessage = "";
        int numberOfColumns = 0;
        List<List<List<Object>>> sheetListData = new ArrayList<List<List<Object>>>();
        int noOfSheets = testDataWorkBook.getNumberOfSheets();
        for (int i = 0; i < noOfSheets; i++) {
            List<List<Object>> sheetData = new ArrayList<List<Object>>();
            Sheet sheet = testDataWorkBook.getSheetAt(i);
            int numberOfRows = getNumberOfNonEmptyRows(sheet);
            for (int j = 1; j < numberOfRows; j++) {
                Row row = sheet.getRow(j);
                if (row == null) {
                    continue;
                }
                numberOfColumns = sheet.getRow(0).getLastCellNum();
                ArrayList<Object> singleRow = new ArrayList<Object>();
                for (int k = 0; k < numberOfColumns; k++) {
                    Cell cell = row.getCell(k);
                    if (cell != null) {
                        singleRow.add(cellToObject(row.getCell(k), false));
                    } else {
                        singleRow.add("");
                    }

                }
                sheetData.add(singleRow);
            }
            if (sheet.getPhysicalNumberOfRows() > 0) {
                sheetListData.add(sheetData);
            }
        }
        if (errormessage.length() > 1) {
            throw new TestsigmaValidationException(ExceptionErrorCodes.MSG_EXECEL_FILE_CELL_NULL,
                    errormessage + "::" + "contains null");
        }
        return sheetListData;
    }

    protected Object cellToObject(Cell cell, boolean isStr) {
        int type;
        Object result = "";
        type = cell.getCellType();
        switch (type) {

            case Cell.CELL_TYPE_NUMERIC:

                if (isStr) {

                    result = NumberToTextConverter.toText(cell.getNumericCellValue());

                } else {

                    result = cell.getNumericCellValue();

                }
                break;

            case Cell.CELL_TYPE_FORMULA:


                switch (cell.getCachedFormulaResultType()) {

                    case Cell.CELL_TYPE_NUMERIC:
                        if (isStr) {

                            result = NumberToTextConverter.toText(cell.getNumericCellValue());

                        } else {

                            result = cell.getNumericCellValue();

                        }

                        break;
                    case Cell.CELL_TYPE_STRING:
                        result = cell.getRichStringCellValue();
                        break;
                }

                break;
            case Cell.CELL_TYPE_STRING:
                result = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_BLANK:
                result = "";
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                cell.setCellType(Cell.CELL_TYPE_STRING);
                result = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_ERROR:
            default:
                throw new RuntimeException(
                        "There is no support for this type of cell");
        }

        return result;
    }

    private int getNumberOfNonEmptyRows(Sheet sheet) {
        int rowIndex = 0, emptyColumnCheckingIndex = 0;
        Iterator<Cell> iterator = sheet.getRow(0).cellIterator();
        while (iterator.hasNext()) {
            Cell cell = iterator.next();
            if (cell != null && !StringUtils.isEmpty(cell.getStringCellValue())
                    && (cell.getStringCellValue().equalsIgnoreCase("Testcase Name")
                    || cell.getStringCellValue().equalsIgnoreCase("Name"))) {
                break;
            }
            emptyColumnCheckingIndex++;
        }

        for (rowIndex = 1; rowIndex < sheet.getPhysicalNumberOfRows(); rowIndex++) {
            if (sheet.getRow(rowIndex) == null) {
                break;
            }

            if (StringUtils.isEmpty(new DataFormatter().formatCellValue(sheet.getRow(rowIndex).getCell(emptyColumnCheckingIndex))))
                break;
        }
        return rowIndex;
    }

    public void incorrectColumnErrors(List<String> columnNames) {
        XLSUtil wrapper = new XLSUtil();
        wrapper.setStorageService(this.storageServiceFactory.getStorageService());
        Row headerRow = wrapper.getHeaderRow();
        CellStyle headerStyle = XLSUtil.getTableHeaderStyle(wrapper);
        XLSUtil.createColumn(headerRow, 0, "Missing columns", headerStyle);

        CellStyle dataStyle = XLSUtil.getAlignStyle(wrapper);

        for (int i = 0; i < columnNames.size(); i++) {
            Row dataRow = wrapper.getDataRow(wrapper, i + 1);
            XLSUtil.createColumn(dataRow, 0, columnNames.get(i), dataStyle);
        }
        log.error("Incorrect column error found");
    }
}

