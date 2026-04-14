package day52.utilities;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import day52.test_base.BaseClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.ImageHtmlEmail;
import org.apache.commons.mail.resolver.DataSourceUrlResolver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
public class ExtentReportManager implements ITestListener {

    // UI of the report
    public ExtentSparkReporter sparkReporter;

    //populate common info on the report
    public ExtentReports extent;

    // creating test case entries in the report and update status of the test methods
    public ExtentTest test;

    String reportName;

    @Override
    public void onStart(ITestContext context) {
        // Time stamp
        String timestamp = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss").format(new Date());

        // Report name
        reportName = "test-report" + timestamp + ".html";

        //spec:
        sparkReporter = new ExtentSparkReporter(System.getProperty("user.dir") + "/reports/" + reportName);
        // TiTle of report
        sparkReporter.config().setDocumentTitle("Automation Report");
        // name of the report
        sparkReporter.config(). setReportName("Functional Testing");
        sparkReporter.config(). setTheme (Theme.DARK);

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("Application", "OpenCart");
        extent.setSystemInfo("Module", "Admin");
        extent.setSystemInfo("Sub Module", "Customer");
        extent.setSystemInfo("User Name", System.getProperty("user.name"));
        extent.setSystemInfo("Environment","QA");

        String os = context.getCurrentXmlTest().getParameter("os");
        extent.setSystemInfo("OS", os);

        String browser = context.getCurrentXmlTest().getParameter("browser");
        extent.setSystemInfo("Browser", browser);

        String device = context.getCurrentXmlTest().getParameter("device");
        if (device != null) {
            extent.setSystemInfo("Device", device);
        }

        List<String> includeGroups = context.getCurrentXmlTest().getIncludedGroups();
        if (!includeGroups.isEmpty()) {
            extent.setSystemInfo("Included Groups", String.join(", ", includeGroups.toString()));
        }

        List<String> excludeGroups = context.getCurrentXmlTest().getExcludedGroups();
        if (!excludeGroups.isEmpty()) {
            extent.setSystemInfo("Excluded Groups", String.join(", ", excludeGroups.toString()));
        }

    }

    @Override
    public void onTestSuccess (ITestResult result){
        // create a new entry in the report
        test = extent.createTest(result.getMethod().getMethodName());

        // Assign category
        test.assignCategory(result.getMethod().getGroups());

        // update status -> Passed/Failed/Skipped
        test.log(Status.PASS, result.getName() + " Test case was successfully PASSED");
    }

    @Override
    public void onTestFailure (ITestResult result) {
        test = extent.createTest(result.getMethod().getMethodName());

        // Assign category
        test.assignCategory(result.getMethod().getGroups());

        // Log failure details first
        test.log(Status.FAIL, result.getName() + " Test case FAILED");

        // Log failure cause
        test.log(Status.INFO, "Test Case FAILED cause is: " + result.getThrowable().getMessage());

        // Add screenshot after all text details
        try {
                String imagePath = new BaseClass().captureScreen(result.getName());
                // Add screenshot with a label to ensure proper positioning
                test.log(Status.INFO, "Screenshot captured at failure:");
                test.addScreenCaptureFromPath(imagePath);
        } catch (IOException e) {
            test.log(Status.WARNING, "Failed to capture screenshot: " + e.getMessage());
        }
    }

    @Override
    public void onTestSkipped (ITestResult result) {
        test = extent.createTest(result.getMethod().getMethodName());

        // Assign category
        test.assignCategory(result.getMethod().getGroups());

        test.log(Status.SKIP, result.getName() + "Test case SKIPPED");
        test.log(Status.INFO, "Test Case SKIPPED cause is: " + result.getThrowable().getMessage());
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();

        String reportPath = System.getProperty("user.dir") + "/reports/" + reportName;
        System.out.println("Extent Report Path: " + reportPath);

        File extentReport = new File(reportPath);
        try {
            // Open extent report on browser automatically
            Desktop.getDesktop().browse(extentReport.toURI());
        } catch (IOException e) {
            System.out.println("Failed to open report: " + e.getMessage());
        }


        try {
            URL url = new URL("file:///" + System.getProperty("user.dir") + "/reports/" + reportName);

            // Create the email message
            ImageHtmlEmail email = new ImageHtmlEmail();
            email.setDataSourceResolver(new DataSourceUrlResolver(url));
            email.setHostName("smtp.googlemail.com");
            email.setSmtpPort(465);
            email.setAuthenticator(new DefaultAuthenticator("gouma308@gmail.com", "pegsvhboaobgosox"));
            email.setSSLOnConnect(true);
            email.setFrom("gouma308@gmail.com"); // Sender
            email.setSubject("Test Results");
            email.setMsg("Please find Attached Report....");
            email.addTo("gouma308@gmail.com"); // Receiver
            email.attach(url, "extent report", "please check report...");
            email.send(); // send the email
        }
        catch (Exception e) {
            log.error("Failed to send email: " + e.getMessage());
        }

    }
}