package day40;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayNameGeneration(CustomDisplayNameGenerator.class)
@Slf4j
public class HandlingXLSXFileTest {

    XSSFSheet sheet;

    @BeforeEach
    void set(){
        log.info("\n--------------------------------------------------------------------------------------------------");
    }
    @Test
    void givenWorkbook_whenCountRowAndCell_thenSuccess() throws IOException {

        // Setup the workbook
        setUpWorkbook();

        // Access rows
        int rowCount = sheet.getLastRowNum(); // count index from 1

        // Access cells
        int cellNum = sheet.getRow(0).getLastCellNum(); // count index from 0

        // Log and assert
        log.info("Row count: {}", rowCount); // 10
        assertEquals(10, rowCount);
        log.info("Cell count: {}", cellNum); // 8
        assertEquals(8, cellNum);
    }

    @Test
    void givenWorkbook_whenReadData_thenSuccess() throws IOException {

        // Set up the workbook
        setUpWorkbook();

        // Access rows
        int rowCount = sheet.getLastRowNum(); // count index from 1

        // Access cells
        int cellNum = sheet.getRow(0).getLastCellNum(); // count index from 0

        // Loop though rows and cells
        for (int row = 0; row <= rowCount; row++) {
            XSSFRow currentRow = sheet.getRow(row);
            for (int cell = 0; cell < cellNum; cell++) {
                XSSFCell currentCell = currentRow.getCell(cell);
                System.out.printf("%-15s", currentCell);
            }
            System.out.println();
        }
    }

    @Test
    void givenHardCodedData_whenWriteToXLSXFile_thenSuccess() throws IOException {
        String filePath = System.getProperty("user.dir") + "/src/test/resources/myFile.xlsx";
        File fileDir = new File(filePath).getParentFile();
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        try (FileOutputStream file = new FileOutputStream(filePath)) {
            try (XSSFWorkbook workbook = new XSSFWorkbook()) {
                sheet = workbook.createSheet("Data");
                XSSFRow row1 = sheet.createRow(0);
                row1.createCell(0).setCellValue("Name");
                row1.createCell(1).setCellValue("Version");
                row1.createCell(2).setCellValue("Purpose");

                XSSFRow row2 = sheet.createRow(1);
                row2.createCell(0).setCellValue("Java");
                row2.createCell(1).setCellValue("26");
                row2.createCell(2).setCellValue("Automation");

                XSSFRow row3 = sheet.createRow(2);
                row3.createCell(0).setCellValue("Python");
                row3.createCell(1).setCellValue("3.11");
                row3.createCell(2).setCellValue("Automation");

                XSSFRow row4 = sheet.createRow(3);
                row4.createCell(0).setCellValue("C#");
                row4.createCell(1).setCellValue("11");
                row4.createCell(2).setCellValue("Automation");

                XSSFRow row5 = sheet.createRow(4);
                row5.createCell(0).setCellValue("C++");
                row5.createCell(1).setCellValue("20");
                row5.createCell(2).setCellValue("Automation");

                XSSFRow row6 = sheet.createRow(5);
                row6.createCell(0).setCellValue("JavaScript");
                row6.createCell(1).setCellValue("2023");
                row6.createCell(2).setCellValue("Automation");

                workbook.write(file);
            }
            log.info("File created and data written to file successfully");

            // Read data from "myFile.xlsx" file
            // Access rows
            int rowCount = sheet.getLastRowNum(); // count index from 1

            // Access cells
            int cellNum = sheet.getRow(0).getLastCellNum(); // count index from 0

            // Call method to read data from the file
            readDataFromXLSXFile(rowCount, cellNum);

            // Log and assert
            log.info("Row count: {}", rowCount); // 5
            assertEquals(5, rowCount);
            log.info("Cell count: {}", cellNum); // 3
            assertEquals(3, cellNum);
        } catch (Exception e) {
            log.error("Exception", e);
            throw e;
        }
    }

    @Test
    void givenUserInputData_whenWriteToXLSXFile_thenSuccess() throws IOException {
        String filePath = System.getProperty("user.dir") + "/src/test/resources/myDynamicDataFile.xlsx";
        File fileDir = new File(filePath).getParentFile();
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        try (FileOutputStream file = new FileOutputStream(filePath)) {
            try (XSSFWorkbook workbook = new XSSFWorkbook()) {
                sheet = workbook.createSheet("DynamicData");

                // Predefined test data instead of interactive input
                String[] testData = {"3", "4", "Name", "Age", "City", "Country",
                        "John", "25", "NYC", "USA",
                        "Jane", "30", "LA", "USA",
                        "Bob", "35", "Chicago", "USA"};
                Scanner userInput = new Scanner(String.join(" ", testData));

                System.out.println("Enter how many rows you want in the excel sheet: ");
                int rowCount = userInput.nextInt();
                System.out.println("Enter how many columns you want in the excel sheet: ");
                int cellCount = userInput.nextInt();

                // Use nested for loop to transverse rows and columns
                for (int row = 0; row <= rowCount; row++) {
                    // Create rows
                    XSSFRow currentRow = sheet.createRow(row);
                    for (int col = 0; col < cellCount; col++) {
                        // Create cells
                        XSSFCell cell = currentRow.createCell(col);
                        // Write data
                        cell.setCellValue(userInput.next());
                    }
                }

                workbook.write(file);
            }
            log.info("File created and data written to file successfully");

            // Read data from the file
            // Access rows
            int rowCount = sheet.getLastRowNum(); // count index from 1

            // Access cells
            int cellNum = sheet.getRow(0).getLastCellNum(); // count index from 0

            // Call method to read data from the file
            readDataFromXLSXFile(rowCount, cellNum);

            // Log and assert
            log.info("Row count: {}", rowCount); // 3
            assertEquals(3, rowCount);
            log.info("Cell count: {}", cellNum); // 4
            assertEquals(4, cellNum);
        }
    }

    // Read data from the file
    void readDataFromXLSXFile(int rowCount, int cellNum) {
        // Access rows
        rowCount = sheet.getLastRowNum(); // count index from 1

        // Access cells
        cellNum = sheet.getRow(0).getLastCellNum(); // count index from 0

        // Loop though rows and cells
        for (int row = 0; row <= rowCount; row++) {
            XSSFRow currentRow = sheet.getRow(row);
            for (int cell = 0; cell < cellNum; cell++) {
                XSSFCell currentCell = currentRow.getCell(cell);
                System.out.printf("%-15s", currentCell);
            }
            System.out.println();
        }
    }

    // Method to set up the workbook
    void setUpWorkbook() throws IOException {
        // Excel file ---> Workbook ---> Sheet ---> Rows ---> Cells
        try (InputStream filePath = getClass().getClassLoader().getResourceAsStream("student_scores.xlsx")) {
            if (filePath == null) {
                throw new FileNotFoundException("Resource not found: student_scores.xlsx");
            }
            XSSFWorkbook workbook = new XSSFWorkbook(filePath);
            sheet = workbook.getSheetAt(0);
        } catch (Exception e) {
            log.error("Exception", e);
            throw e;
        }
    }
}