package day46;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ExtentReportManager implements ITestListener {

    // UI of the report
    public ExtentSparkReporter sparkReporter;

    //populate common info on the report
    public ExtentReports extent;

    // creating test case entries in the report and update status of the test methods
    public ExtentTest test;

    @Override
    public void onStart(ITestContext context) {
        //spec:
        sparkReporter = new ExtentSparkReporter(System.getProperty("user.dir") + "/src/test/reports/myReport.html");
        // TiTle of report
        sparkReporter.config().setDocumentTitle("Automation Report");
        // name of the report
        sparkReporter.config(). setReportName("Functional Testing");
        sparkReporter.config(). setTheme (Theme.DARK);

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("Computer Name", "localhost");
        extent.setSystemInfo("Environment","QA");
        extent.setSystemInfo("Tester Name", "Yerfdog");
        extent.setSystemInfo("os", "macOS");
        extent.setSystemInfo("Browser name", "Chrome");
    }

    @Override
    public void onTestSuccess (ITestResult result){
        // create a new entry in the report
        test = extent.createTest(result.getName());
        // update status p/f/s
        test.log(Status.PASS, "Test case PASSED is:" + result.getName());
    }

    @Override
    public void onTestFailure (ITestResult result) {
        test = extent.createTest(result.getName()) ;
        test.log(Status.FAIL, "Test case FAILED is:" + result.getName());
        test.log(Status.FAIL, "Test Case FAILED cause is: " + result.getThrowable()) ;
    }

    @Override
    public void onTestSkipped (ITestResult result) {
        test = extent.createTest(result.getName());
        test.log(Status.SKIP, "Test case SKIPPED is:" + result. getName()) ;
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }
}