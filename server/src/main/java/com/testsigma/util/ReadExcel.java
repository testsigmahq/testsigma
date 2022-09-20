package com.testsigma.util;

import com.testsigma.exception.ExceptionErrorCodes;
import com.testsigma.exception.TestsigmaValidationException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class ReadExcel {


    public static String FILE_TYPE_XLS = "xls";
    public static String FILE_TYPE_XLSX = "xlsx";

    public static Workbook getExcelWorkBook(MultipartFile multiPartFile)
            throws IOException, TestsigmaValidationException {
        Workbook workbook = null;
        InputStream exelFileInputStream = multiPartFile.getInputStream();
        if (multiPartFile.getOriginalFilename().endsWith(FILE_TYPE_XLS)) {
            try {
                workbook = new HSSFWorkbook(exelFileInputStream);
            } catch (Exception e) {
                throw new TestsigmaValidationException(ExceptionErrorCodes.MSG_INVALID_EXCEL_FILE_TYPE, multiPartFile.getOriginalFilename());
            }
        } else if (multiPartFile.getOriginalFilename().endsWith(FILE_TYPE_XLSX)) {
            try {
                workbook = new XSSFWorkbook(exelFileInputStream);
            } catch (Exception e) {
                throw new TestsigmaValidationException(ExceptionErrorCodes.MSG_INVALID_EXCEL_FILE_TYPE, multiPartFile.getOriginalFilename());
            }
        } else {
            throw new TestsigmaValidationException(ExceptionErrorCodes.MSG_INVALID_EXCEL_FILE_TYPE, multiPartFile.getOriginalFilename());
        }
        return workbook;
    }

    public static Workbook getExcelWorkBook(String path)
            throws Exception {
        Workbook workbook = null;
        InputStream is = ReadExcel.class.getClassLoader().getResourceAsStream(path);
        if (path.endsWith(FILE_TYPE_XLS)) {
            workbook = new HSSFWorkbook(is);
        } else if (path.endsWith(FILE_TYPE_XLSX)) {
            workbook = new XSSFWorkbook(is);
        } else {
            throw new TestsigmaValidationException(ExceptionErrorCodes.MSG_INVALID_EXCEL_FILE_TYPE, path);
        }
        return workbook;
    }

    public static List<List<List<Object>>> getExelDataList(
            Workbook testDataWorkBook) throws Exception {
        String errormessage = "";
        int numberOfColumns = 0;
        List<List<List<Object>>> sheetListData = new ArrayList<List<List<Object>>>();
        int noOfSheets = testDataWorkBook.getNumberOfSheets();
        for (int i = 0; i < noOfSheets; i++) {
            List<List<Object>> sheetData = new ArrayList<List<Object>>();
            Sheet sheet = testDataWorkBook.getSheetAt(i);
            int numberOfRows = sheet.getPhysicalNumberOfRows();
            for (int j = 1; j < numberOfRows; j++) {
                Row row = sheet.getRow(j);
                if (row != null) {
                    numberOfColumns = row.getPhysicalNumberOfCells();
                } else {
                    throw new TestsigmaValidationException(ExceptionErrorCodes.MSG_EXECEL_FILE_ROW_NULL, j + "");
                }
                ArrayList<Object> singleRow = new ArrayList<Object>();
                for (int k = 0; k < numberOfColumns; k++) {
                    Cell cell = row.getCell(k);
                    if (cell != null) {
                        singleRow.add(cellToObject(row.getCell(k), false));
                    } else {
                        errormessage = (j + 1) + ":" + (k + 1) + "," + errormessage;
                    }

                }
                sheetData.add(singleRow);
            }
            if (sheet.getPhysicalNumberOfRows() > 0)
                sheetListData.add(sheetData);
        }
        if (errormessage.length() > 1) {
            throw new TestsigmaValidationException(ExceptionErrorCodes.MSG_EXECEL_FILE_CELL_NULL, errormessage + "::" + "contains null");
        }
        return sheetListData;
    }


    public static List<List<List<Object>>> getExelDataList(
            Workbook testDataWorkBook, Collection<List<Object>> fileList) throws Exception {
        String errormessage = "";
        int numberOfColumns = 0;
        List<List<List<Object>>> sheetListData = new ArrayList<List<List<Object>>>();
        int noOfSheets = testDataWorkBook.getNumberOfSheets();
        for (int i = 0; i < noOfSheets; i++) {
            List<List<Object>> sheetData = new ArrayList<List<Object>>();
            Sheet sheet = testDataWorkBook.getSheetAt(i);
            int numberOfRows = sheet.getPhysicalNumberOfRows();
            for (int j = 1; j < numberOfRows; j++) {
                Row row = sheet.getRow(j);
                ArrayList<Object> singleRow = new ArrayList<Object>();
                if (row != null) {
                    numberOfColumns = (fileList).iterator().next().size();//row.getPhysicalNumberOfCells();

                    for (int k = 0; k < numberOfColumns; k++) {
                        Cell cell = row.getCell(k);
                        if (cell != null) {
                            singleRow.add(cellToObject(row.getCell(k), true));
                        } else {
                            singleRow.add("");
                        }

                    }
                    sheetData.add(singleRow);
                }

            }
            if (sheet.getPhysicalNumberOfRows() > 0)
                sheetListData.add(sheetData);
        }
        if (errormessage.length() > 1) {
            throw new TestsigmaValidationException(ExceptionErrorCodes.MSG_EXECEL_FILE_CELL_NULL, errormessage + "::" + "contains null");
        }
        return sheetListData;
    }


    public static Map<String, List<Object>> getExelFieldNames(Workbook testDataWorkBook) throws TestsigmaValidationException, UnsupportedEncodingException {
        int noOfSheets = testDataWorkBook.getNumberOfSheets();
        Map<String, List<Object>> sheetData = new HashMap<>();
        if (noOfSheets == 1) {
            for (int i = 0; i < noOfSheets; i++) {
                Sheet sheet = testDataWorkBook.getSheetAt(i);
                int numberOfRows = sheet.getPhysicalNumberOfRows();
                int j = 0;
                if (j < 1) {
                    Row row = sheet.getRow(j);
                    int numberOfColumns = row.getPhysicalNumberOfCells();
                    List<Object> singleRow = new ArrayList<Object>();
                    for (int k = 0; k < numberOfColumns; k++) {
                        Cell cell = row.getCell(k);
                        if (cell.getCellType() == Cell.CELL_TYPE_BLANK && org.apache.commons.lang3.StringUtils.isEmpty(cell.getStringCellValue())) {
                            throw new TestsigmaValidationException(ExceptionErrorCodes.TEST_DATA_HEADER_INVALID);
                        } else {
                            singleRow.add(cellToObject(row.getCell(k), false));
                        }
                    }
                    sheetData.put(testDataWorkBook.getSheetName(i), singleRow);
                }
            }
        } else {
            throw new TestsigmaValidationException(ExceptionErrorCodes.MSG_MULTIPLE_EXECEL_FILE, "Sheets::" + testDataWorkBook.getNumberOfSheets());
        }

        return sheetData;
    }


    public static Map<String, List<Object>> getNlpExelFieldNames(Workbook testDataWorkBook) throws Exception {

        int noOfSheets = testDataWorkBook.getNumberOfSheets();
        Map<String, List<Object>> sheetData = new HashMap<String, List<Object>>();
        for (int i = 0; i < noOfSheets; i++) {
            Sheet sheet = testDataWorkBook.getSheetAt(i);
            int j = 0;
            if (j < 1) {
                Row row = sheet.getRow(j);
                int numberOfColumns = row.getPhysicalNumberOfCells();
                List<Object> singleRow = new ArrayList<Object>();
                for (int k = 0; k < numberOfColumns; k++) {

                    singleRow.add(cellToObject(row.getCell(k), false));
                }
                sheetData.put(testDataWorkBook.getSheetName(i), singleRow);
            }
        }

        return sheetData;
    }

    public static Map<String, List<List<Object>>> getExelFieldsList(Workbook testDataWorkBook) throws Exception {
        Map<String, List<List<Object>>> sheetListData = new HashMap<String, List<List<Object>>>();
        int noOfSheets = testDataWorkBook.getNumberOfSheets();
        for (int i = 0; i < noOfSheets; i++) {
            List<List<Object>> sheetData = new ArrayList<List<Object>>();
            Sheet sheet = testDataWorkBook.getSheetAt(i);
            int numberOfRows = sheet.getPhysicalNumberOfRows();
            int j = 0;
            if (j < numberOfRows) {
                Row row = sheet.getRow(j);
                int numberOfColumns = row.getPhysicalNumberOfCells();
                ArrayList<Object> singleRow = new ArrayList<Object>();
                for (int k = 0; k < numberOfColumns; k++) {
                    Cell cell = row.getCell(k);
                    singleRow.add(cellToObject(cell, false));
                }
                sheetData.add(singleRow);
            }
            if (sheet.getPhysicalNumberOfRows() > 0)
                sheetListData.put(testDataWorkBook.getSheetName(i), sheetData);
        }

        return sheetListData;
    }

    public static Object cellToObject(Cell cell, boolean isStr) throws UnsupportedEncodingException {
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

    public static String sheetName(Workbook testDataWorkBook, int sheetNo) throws Exception {
        String name = null;
        if (testDataWorkBook.getNumberOfSheets() > sheetNo) {
            Sheet sheet = testDataWorkBook.getSheetAt(sheetNo);
            name = sheet.getSheetName();
        }
        return name;
    }

    //nlpstepTemplets excel files
    public static String getnlpStepTempletMap(
            List<List<List<Object>>> values, Object[] fixedValues)
            throws Exception {

        String toReturn = null;
        for (int i = 0; (i < 1 && i < values.size()); i++) {
            List<List<Object>> sheetrow = values.get(i);
            List<Object[]> rows = new ArrayList<Object[]>();
            for (int j = 0; j < sheetrow.size(); j++) {
                List<Object> row = sheetrow.get(j);
                Object[] columns = getStringArray(row.toArray());
                rows.add(columns);
            }
            toReturn = ReadExcel.getDBString(rows, fixedValues);
        }
        return toReturn;
    }

    public static String getDBValuesString(Object[] idsarr) {
        String toReturn = "";
        for (Object fid : idsarr) {
            if (fid instanceof String) {
                String value = fid.toString();
                toReturn = toReturn + ",'" + value + "'";
            } else {

                if (fid == null)
                    toReturn = toReturn + "," + null;
                else
                    toReturn = toReturn + "," + fid;
            }
        }
        if (toReturn.length() > 0)
            toReturn = toReturn.substring(1);
        toReturn = "(" + toReturn + ")";
        return toReturn;

    }

    public static String getDBString(List<Object[]> fEntity, Object[] fixedValues) {

        String toReturn = "";
        String fixedString = getDBValuesString(fixedValues);
        if (fixedString.length() > 2)
            fixedString = fixedString.substring(1, fixedString.length() - 1);
        for (Object[] fid : fEntity) {
            toReturn = new StringBuffer().append("(").append("'").append(fid[0]).append("','").append(fid[1])
                    .append("','").append(fid[2]).append("','").append(fid[3]).append("','").append(fid[4])
                    .append("','").append(fid[5]).append("','").append(fid[6]).append("',")
                    .append(fid[7]).append(",").append(fid[8]).append(",'").append(fid[9]).append("','").append(fid[10]).append("','")
                    .append(fid[11]).append("',").append(fixedString).append("),") + toReturn;
        }
        toReturn = toReturn.substring(0, toReturn.length() - 1);
        return toReturn;
    }

    public static String[] getStringArray(Object[] objs) {
        String[] toReturn = new String[objs.length];
        for (int i = 0; i < objs.length; i++) {
            toReturn[i] = objs[i].toString();
        }
        return toReturn;
    }

}

