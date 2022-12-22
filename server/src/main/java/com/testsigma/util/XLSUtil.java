/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.util;

import com.testsigma.model.StorageAccessLevel;
import com.testsigma.service.AwsS3StorageService;
import com.testsigma.service.StorageService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Log4j2
public class XLSUtil {

    private SXSSFWorkbook workbook = null;
    private Sheet sheet = null;
    private int row = 1;
    private CreationHelper createHelper = null;
    private Boolean status = false;
    private CellStyle cellDateStyle = null;

    @Setter
    @Getter
    private StorageService storageService;

    /**
     *
     */
    public XLSUtil() {
        workbook = new SXSSFWorkbook(100);
        sheet = workbook.createSheet();
        createHelper = workbook.getCreationHelper();
        initStyles();
    }

    /**
     * creates, assigns value and apply styles to Cell
     *
     * @param headingRow
     * @param columnCount
     * @param value
     * @param colorStyle
     * @return Cell
     */
    public static Cell createColumn(Row headingRow, int columnCount, Object value, CellStyle colorStyle) {
        Cell sectionNameCell = setValue(headingRow, columnCount, value);
        sectionNameCell.setCellStyle(colorStyle);
        return sectionNameCell;
    }

    /**
     * Set cell value based on type
     *
     * @param row
     * @param index
     * @param value
     * @return Cell
     */
    private static Cell setValue(Row row, int index, Object value) {
        Cell cell = row.createCell(index);
        if (value != null) {
            if (value.getClass().equals(String.class)) {
                cell.setCellValue((String) value);
            } else if (value.getClass().equals(Integer.class)) {
                cell.setCellValue((Integer) value);
            } else if (value.getClass().equals(Long.class)) {
                cell.setCellValue((Long) value);
            } else if (value.getClass().equals(Double.class)) {
                cell.setCellValue((Double) value);
            } else if (value.getClass().equals(Date.class)) {
                cell.setCellValue((Date) value);
            } else if (value.getClass().equals(Float.class)) {
                cell.setCellValue((Float) value);
            }
        }
        return cell;
    }

    /**
     * Returns header style with Black background
     *
     * @param wrapper
     * @return CellStyle
     */
    public static CellStyle getTableHeaderStyle(XLSUtil wrapper) {
        CellStyle colorStyle = wrapper.getWorkbook().createCellStyle();
        //set background color black
        colorStyle.setWrapText(true);
        colorStyle.setFillForegroundColor(HSSFColor.BLACK.index);
        colorStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        //text color white and style bold
        Font font = wrapper.getWorkbook().createFont();
        font.setColor(HSSFColor.WHITE.index);
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        colorStyle.setFont(font);
        font.setBold(true);

        //align center
        colorStyle.setAlignment(CellStyle.ALIGN_CENTER);
        colorStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        return colorStyle;
    }

    /**
     * Return vertical center align style
     *
     * @param wrapper
     * @return CellStyle
     */
    public static CellStyle getAlignStyle(XLSUtil wrapper) {
        CellStyle style = wrapper.getWorkbook().createCellStyle();
        style.setWrapText(true);
        style.setAlignment(CellStyle.ALIGN_LEFT);
        return style;
    }

    public static CellStyle getSecondAlignStyle(XLSUtil wrapper) {
        CellStyle style = wrapper.getWorkbook().createCellStyle();
        style.setWrapText(true);
        style.setAlignment(CellStyle.ALIGN_LEFT);
        Font font = wrapper.getWorkbook().createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style.setFont(font);

        return style;
    }

    /**
     *
     */
    public void initStyles() {

        if (workbook != null) {
            cellDateStyle = workbook.createCellStyle();
            cellDateStyle.setDataFormat(createHelper.createDataFormat().getFormat("mmm-yy"));
        }
    }

    /**
     * @return Row
     */
    public Row getHeaderRow() {

        Row row = this.getSheet().getRow(0);
        if (null == row) {
            row = this.getSheet().createRow(0);
        }

        return row;
    }

    public Row getDataRow(XLSUtil excel, int newRowIndex) {

        Row row = excel.getSheet().getRow(newRowIndex);
        if (null == row) {
            row = excel.getSheet().createRow(newRowIndex);
        }

        return row;
    }

    /**
     * @return CellStyle
     */
    public CellStyle getHeaderStyle() {

        CellStyle style = this.getWorkbook().createCellStyle();
        getHeaderRow().setRowStyle(style);

        Font font = this.getWorkbook().createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName("Arial");
        font.setColor(IndexedColors.WHITE.getIndex());

        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font.setItalic(false);

        style = getHeaderRow().getRowStyle();
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setFillBackgroundColor(IndexedColors.AQUA.getIndex());
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setFont(font);
        style.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);

        return style;

    }

    /**
     * @return CellStyle
     */
    public CellStyle getCellDateStyle() {
        return cellDateStyle;
    }

    /**
     * @param dateStr
     * @return Date
     */
    public Date getDateType(String dateStr) {
        Date date = new Date();
        date.setTime(0);
        if (null != dateStr) {

            try {
                date = new SimpleDateFormat("MMM-yy", Locale.US).parse(dateStr);
            } catch (ParseException e) {
                try {
                    date = new SimpleDateFormat("MMMMM yyyy", Locale.US).parse(dateStr);
                } catch (ParseException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

            }
        }

        return date;
    }

    public String getRunDate() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("MM/dd/yyyy");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

    public String getRunTime() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("h:mm a");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

    public void evaluateAll() {

        if (createHelper != null) {
            FormulaEvaluator evaluator = createHelper.createFormulaEvaluator();
            evaluator.evaluateAll();
        }

    }

    /**
     * @return SXSSFWorkbook
     */
    public SXSSFWorkbook getWorkbook() {
        return workbook;
    }

    /**
     * @return Sheet
     */
    public Sheet getSheet() {
        return sheet;
    }

    /**
     * @param sheet
     */
    public void setSheet(Sheet sheet) {
        this.sheet = sheet;
    }

    /**
     * @param name
     * @return Sheet
     */
    public Sheet createSheet(String name) {
        this.sheet = this.getWorkbook().createSheet(name);
        return this.sheet;
    }

    public Sheet createSheet() {
        this.sheet = this.getWorkbook().createSheet();
        return this.sheet;
    }

    /**
     * @return int
     */
    public int getCurrentRow() {
        return row;
    }

    /**
     * @param rowNum
     */
    public void setCurrentRow(int rowNum) {
        this.row = rowNum;
    }

    /**
     * @return int
     */
    public int getNewRow() {
        this.row = this.row + 1;
        return this.row;
    }

    /**
     * @param inc
     */
    public void incRow(int inc) {
        this.row = this.row + inc;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public void writeToStream(HttpServletRequest request, HttpServletResponse response, String name) {

        String browserType = getBrowserName(request);

        try {
            String fileName = URLEncoder.encode(name + ".xls", StandardCharsets.UTF_8);
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("UTF-8");
            if (browserType.equals("IE") || browserType.equals("Chrome")) {
                response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            }
            if (browserType.endsWith("Firefox")) {
                response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);
            }
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            workbook.write(response.getOutputStream());
            response.flushBuffer();
            response.getOutputStream().flush();
            response.getOutputStream().close();
        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    private String getBrowserName(HttpServletRequest request) {

        String userAgent = request.getHeader("user-agent");

        if (userAgent.contains("Chrome")) {
            return "Chrome";
        } else if (userAgent.contains("Firefox")) {
            return "Firefox";
        } else {
            return "IE";
        }

    }

    public String writeToStorage() {
        ByteArrayOutputStream bos = null;
        InputStream is = null;
        try {
            String fileName = System.currentTimeMillis() + ".xls";
            bos = new ByteArrayOutputStream();
            workbook.write(bos);
            byte[] barray = bos.toByteArray();
            is = new ByteArrayInputStream(barray);
            String filePath =  "/export_xlsx/" + fileName;
            this.storageService.addFile(filePath, is);
            return this.storageService.generatePreSignedURL(filePath, StorageAccessLevel.FULL_ACCESS, 180).toString();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                assert bos != null;
                bos.close();
                assert is != null;
                is.close();
            } catch (Exception ignored) {
            }
        }
        return null;
    }

}
