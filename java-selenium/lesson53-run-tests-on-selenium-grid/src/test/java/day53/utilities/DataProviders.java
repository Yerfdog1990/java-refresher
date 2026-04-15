package day53.utilities;

import org.testng.annotations.DataProvider;

import java.io.IOException;

public class DataProviders {
    //DataProvider 1
    @DataProvider(name="LoginData")
    public String[][] getData() throws IOException {
        // taking x1 file from testData Excel utility
        String path = System.getProperty("user.dir") + "/test_data/Opencart_loginData.xlsx";

        // creating an object for ExcelUtility
        ExcelUtility xlUtil = new ExcelUtility(path);
        int totalRows = xlUtil.getRowCount("Sheet1");
        int totalCols = xlUtil.getCellCount("Sheet1", 1);
        // creating for the two-dimension array which can store
        String[][] loginData = new String[totalRows][totalCols];
        for (int i = 1; i < totalRows; i++) { // 1 read the data from MS Excel storing i
            for (int j = 0; j < totalCols; j++) { // 0 i is row and j is column
                loginData[i-1][j] = xlUtil.getCellData("Sheet1", i, j ); // 1, 0
            }
        }
        // returning the two-dimension array
        return loginData;
    }
}
