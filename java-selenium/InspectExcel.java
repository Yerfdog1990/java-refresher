
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;

public class InspectExcel {
    public static void main(String[] args) {
        String filePath = "lesson41-data-driven-testing-with-ms-excel/src/test/resources/test_data.xlsx";
        try (FileInputStream fis = new FileInputStream(filePath);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {
            System.out.println("Number of sheets: " + workbook.getNumberOfSheets());
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                System.out.println("Sheet name at index " + i + ": " + workbook.getSheetName(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
