package day41;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Objects;

public class ExcelUtils {
    public static FileInputStream fileIn;
    public static FileOutputStream fileOut;
    public static XSSFWorkbook workbook;
    public static XSSFSheet sheet;
    public static XSSFRow row;
    public static XSSFCell cell;
    public static XSSFCellStyle style;

    // Method to get the number of rows in the sheet
    public static int getRowCount(String xlFile, int xlSheet) {
        try(FileInputStream fileIn = new FileInputStream(xlFile)){
            workbook = new XSSFWorkbook(fileIn);
            sheet = workbook.getSheetAt(xlSheet);
            Objects.requireNonNull(sheet, "Sheet index '" + xlSheet + "' not found in file: " + xlFile);
            return sheet.getLastRowNum();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Method to get the number of cells in the sheet
    public static int getCellCount(String xlFile, int xlSheet, int rowNum){
        try(FileInputStream fileIn = new FileInputStream(xlFile)){
            workbook = new XSSFWorkbook(fileIn);
            sheet = workbook.getSheetAt(xlSheet);
            Objects.requireNonNull(sheet, "Sheet index '" + xlSheet + "' not found in file: " + xlFile);
            row = sheet.getRow(rowNum);
            if (row == null) return 0;
            return row.getLastCellNum();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Method to get cell data in the sheet
    public static String getCellData(String xlFile, int xlSheet, int rowNum, int column){
        try(FileInputStream fileIn = new FileInputStream(xlFile)){
            workbook = new XSSFWorkbook(fileIn);
            sheet = workbook.getSheetAt(xlSheet);
            Objects.requireNonNull(sheet, "Sheet index '" + xlSheet + "' not found in file: " + xlFile);
            row = sheet.getRow(rowNum);
            if (row == null) return "";
            cell = row.getCell(column);

            String data;
            try {
                DataFormatter formatter = new DataFormatter();
                data = formatter.formatCellValue(cell);
            } catch (Exception e) {
                data = "";
            }
            return data;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Method to set cell data in the sheet
    public static void setCellData(String xlFile, int xlSheet, int rowNum, int column, String data){
        try(FileInputStream fileIn = new FileInputStream(xlFile)){
            // Read cell data
            workbook = new XSSFWorkbook(fileIn);
            sheet = workbook.getSheetAt(xlSheet);
            Objects.requireNonNull(sheet, "Sheet index '" + xlSheet + "' not found in file: " + xlFile);
            row = sheet.getRow(rowNum);
            if (row == null) {
                row = sheet.createRow(rowNum);
            }

            // Write cell data
            cell = row.createCell(column);
            cell.setCellValue(data);
            try (FileOutputStream fileOut = new FileOutputStream(xlFile)) {
                workbook.write(fileOut);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Method to fill the cell with a green color if the test passed
    public static void fillGreenColor(String xlFile, int xlSheet, int rowNum, int column){
        try(FileInputStream fileIn = new FileInputStream(xlFile)){
            workbook = new XSSFWorkbook(fileIn);
            sheet = workbook.getSheetAt(xlSheet);
            Objects.requireNonNull(sheet, "Sheet index '" + xlSheet + "' not found in file: " + xlFile);
            row = sheet.getRow(rowNum);
            if (row == null) return;
            cell = row.getCell(column);
            if (cell == null) return;

            // Add cell style
            style = workbook.createCellStyle();
            style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Apply cell style
            cell.setCellStyle(style);
            try (FileOutputStream fileOut = new FileOutputStream(xlFile)) {
                workbook.write(fileOut);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Method to fill the cell with a red color if the test passed
    public static void fillRedColor(String xlFile, int xlSheet, int rowNum, int column){
        try(FileInputStream fileIn = new FileInputStream(xlFile)){
            workbook = new XSSFWorkbook(fileIn);
            sheet = workbook.getSheetAt(xlSheet);
            Objects.requireNonNull(sheet, "Sheet index'" + xlSheet + "' not found in file: " + xlFile);
            row = sheet.getRow(rowNum);
            if (row == null) return;
            cell = row.getCell(column);
            if (cell == null) return;

            // Add cell style
            style = workbook.createCellStyle();
            style.setFillForegroundColor(IndexedColors.RED.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Apply cell style
            cell.setCellStyle(style);
            try (FileOutputStream fileOut = new FileOutputStream(xlFile)) {
                workbook.write(fileOut);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
