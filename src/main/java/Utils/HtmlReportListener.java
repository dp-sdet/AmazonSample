package Utils;

/**
 * Class to configure reporting parameters in Hawk Framework
 */
import exception.ItJumpStartReportException;
import exception.ReportConfigException;
import exception.ReportCreationException;
import report.LoggerUtil;
import report.MainReporting;
import report.PropLoader;
import report.ResultContainer;
import report.VerifySafe;
import Utils.Utils;
import core.EnvParameters;
import core.WebBase;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.IExecutionListener;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.TestNG;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

public class HtmlReportListener implements IInvokedMethodListener, ITestListener, ISuiteListener, IExecutionListener {
	public String sFilename;
	private static final String ESCAPE_PROPERTY = "org.uncommons.reportng.escape-output";
	private static String screenshotPath = "";
	public static String testResultVideos = "";
	private static String resultDir;
	private static boolean runParallel = false;
	private int retryCount = 0;

	public static String itJumpStart_ReportDir;

	private static String outdir;
	public static final String CONFIG_FILE;
	public static final boolean CAPTURE_SCREENSHOT;
	public static final String APPLICATION_NAME;
	public static final String ENVIRONMENT;
	public static final String SUITNAME;
	public static final boolean OPENREPORT;
	public static final boolean GEN_ADVANCED_REPORT;
	public static final boolean ARCHIVE_REPORT;
	private static Properties props = new Properties();

	static {
		String custom_property = System.getProperty("customProperties");
		if (custom_property != null && !custom_property.equalsIgnoreCase("")) {
			CONFIG_FILE = custom_property;
		} else {
			CONFIG_FILE = System.getProperty("user.dir") + File.separator + "config.properties";
		}
		Properties reportProperties = (new PropLoader()).loadProperties(CONFIG_FILE);

		if (System.getProperty("capture.screenshot") != null) {
			CAPTURE_SCREENSHOT = System.getProperty("capture.screenshot").equalsIgnoreCase("true");
		} else if (reportProperties.getProperty("capture.screenshot") != null) {
			CAPTURE_SCREENSHOT = reportProperties.getProperty("capture.screenshot").equalsIgnoreCase("true");
		} else {

			CAPTURE_SCREENSHOT = true;
		}

		if (reportProperties.getProperty("Env") != null && !reportProperties.getProperty("Env").equals("")) {
			ENVIRONMENT = reportProperties.getProperty("Env");
		} else {
			ENVIRONMENT = "Default Environment";
		}

		if (reportProperties.getProperty("suitname") != null && !reportProperties.getProperty("suitname").equals("")) {
			SUITNAME = reportProperties.getProperty("suitname");
		} else {
			SUITNAME = "Default Suite";
		}

		if (reportProperties.getProperty("ApplicationName") != null
				&& !reportProperties.getProperty("ApplicationName").equals("")) {
			APPLICATION_NAME = reportProperties.getProperty("ApplicationName");
		} else {
			APPLICATION_NAME = "Automation Test Execution Report";
		}

		if (reportProperties.getProperty("openreport") != null
				&& !reportProperties.getProperty("openreport").trim().equalsIgnoreCase("")) {
			OPENREPORT = Boolean.parseBoolean(reportProperties.getProperty("openreport"));
		} else {
			OPENREPORT = true;
		}

		if (reportProperties.getProperty("advanced.report") != null
				&& !reportProperties.getProperty("advanced.report").trim().equalsIgnoreCase("")) {
			GEN_ADVANCED_REPORT = Boolean.parseBoolean(reportProperties.getProperty("advanced.report"));
		} else {
			GEN_ADVANCED_REPORT = true;
		}

		if (reportProperties.getProperty("archive.report") != null
				&& !reportProperties.getProperty("archive.report").trim().equalsIgnoreCase("")) {
			ARCHIVE_REPORT = Boolean.parseBoolean(reportProperties.getProperty("archive.report"));
		} else {
			ARCHIVE_REPORT = false;
		}
	}

	public static String getScreenshotPath() {
		return screenshotPath;
	}

	public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {
	}

	public void onStart(ITestContext context) {
		System.setProperty("org.uncommons.reportng.escape-output", "false");
	}

	public void onTestStart(ITestResult iTestResult) {
		String sTestMethodName = iTestResult.getMethod().getMethodName();
		String sTestSuiteName = iTestResult.getTestClass().getRealClass().getSimpleName();
		LoggerUtil.log("=====================================================================================");
		LoggerUtil.log("<<<*** START: " + sTestSuiteName + "." + sTestMethodName + " ***>>> ");
		int invocationCount = iTestResult.getMethod().getCurrentInvocationCount() + 1;
		String browser = iTestResult.getMethod().getXmlTest().getParameter("Browser");

	}

	public void onTestFailure(ITestResult result) {
		String sTestMethodName = result.getMethod().getMethodName();
		String sTestSuiteName = result.getTestClass().getRealClass().getSimpleName();
		String timeTaken = Long.toString((result.getEndMillis() - result.getStartMillis()) / 1000L);
		String testName = result.getTestClass().getName() + "." + result.getMethod().getMethodName();
		int invocationCount = result.getMethod().getCurrentInvocationCount();
		String browser = result.getMethod().getXmlTest().getParameter("Browser");
		String imageFile = "";
		ResultContainer rc = null;
		Object currentClass = result.getInstance();
		WebDriver driver = null;
		Class c = result.getInstance().getClass();
		while (c != null) {
			if (c.getName().contains("WebBase")) {
				driver = ((WebBase) currentClass).getDriver();
				break;
			}
			driver = null;

			c = c.getSuperclass();
		}

		rc = new ResultContainer(driver);
		if (driver != null) {
			if (CAPTURE_SCREENSHOT) {
				rc.setScreenshot();
				rc.setWebpage();

				if (browser == null || browser.equalsIgnoreCase("")) {

					imageFile = screenshotPath + File.separator + sTestMethodName + "-"
							+ String.valueOf(invocationCount) + "-" + Utils.getCurrentDateTime("ddMMMyy_hhmmss")
							+ ".png";
				} else {

					imageFile = screenshotPath + File.separator + sTestMethodName + "-" + browser.replaceAll("\\*", "")
							+ "-" + String.valueOf(invocationCount) + "-" + Utils.getCurrentDateTime("ddMMMyy_hhmmss")
							+ ".png";
				}

				rc.saveScreenshot(imageFile);

				RemoteWebDriver rd = (RemoteWebDriver) driver;
				Capabilities descap = rd.getCapabilities();
				String app = (String) descap.getCapability("app");
				String fileextn = null;
				if (StringUtils.isNotEmpty(app)) {
					fileextn = ".xml";
				} else {
					fileextn = ".html";
				}
				rc.saveWebPage(imageFile.replaceAll(".png", fileextn));
				Reporter.log("<a href='" + Utils.getRelativePath(imageFile, resultDir + File.separator + "html")
						+ "'><img src='" + Utils.getRelativePath(imageFile, resultDir + File.separator + "html")
						+ "' height='50' width='50'/><br></a>");
			} else {

				LoggerUtil.log("***WARN : Screen shot setting is set to false ");
			}
		} else {
			LoggerUtil.log("Webdriver object is not passed to listener", Level.WARN);
		}

		if (rc != null) {
			result.setAttribute("resultcontainer", rc);
		}

		if (GEN_ADVANCED_REPORT) {
			synchronized (this) {
				MainReporting.addTestMethodNode(result);
				MainReporting.reportError(result);
			}
		}

		LoggerUtil.log("<<<*** END: " + sTestSuiteName + "." + sTestMethodName + " ***>>> ");
		LoggerUtil.log("=====================================================================================");
		LoggerUtil.log("Test Failed :" + testName + ", Took " + timeTaken + " seconds");
	}

	public void onTestSkipped(ITestResult iTestResult) {
		String timeTaken = Long.toString((iTestResult.getEndMillis() - iTestResult.getStartMillis()) / 1000L);
		String testName = iTestResult.getTestClass().getName() + "." + iTestResult.getMethod().getMethodName();
		LoggerUtil.log("/////////////////////////////////////////////////////////////////////////////////////////");
		LoggerUtil.log("Test Skipped :" + testName + ", Took " + timeTaken + " seconds");

		if (GEN_ADVANCED_REPORT) {
			synchronized (this) {
				MainReporting.addTestMethodNode(iTestResult);
				StringBuffer methodsDependOn = new StringBuffer();
				for (String str : iTestResult.getMethod().getMethodsDependedUpon()) {
					methodsDependOn.append(str);
					methodsDependOn.append(", ");
				}
				if (!methodsDependOn.toString().equalsIgnoreCase("")) {
					MainReporting.addResultInfoNode(iTestResult,
							"Test has Dependent methods: " + methodsDependOn.substring(0, methodsDependOn.length() - 2),
							4);
				}

				StringBuffer groupsDependedOn = new StringBuffer();
				for (String str : iTestResult.getMethod().getGroupsDependedUpon()) {
					groupsDependedOn.append(str);
					groupsDependedOn.append(", ");
				}
				if (!groupsDependedOn.toString().equalsIgnoreCase("")) {
					MainReporting.addResultInfoNode(iTestResult, "Test has Dependent groups: "
							+ groupsDependedOn.substring(0, groupsDependedOn.length() - 2), 4);
				}
				MainReporting.reportWarning(iTestResult);

			}
		}
	}

	public void onTestSuccess(ITestResult iTestResult) {
		String sTestMethodName = iTestResult.getMethod().getMethodName();
		String sTestSuiteName = iTestResult.getTestClass().getRealClass().getSimpleName();
		String timeTaken = Long.toString((iTestResult.getEndMillis() - iTestResult.getStartMillis()) / 1000L);
		String testName = iTestResult.getTestClass().getName() + "." + iTestResult.getMethod().getMethodName();

		if (this.retryCount > 1) {
			Reporter.log("Test '" + sTestMethodName + "' passed after " + (this.retryCount - 1) + " retrie(s)");
			this.retryCount = 1;
		}

		if (GEN_ADVANCED_REPORT) {
			synchronized (this) {
				MainReporting.addTestMethodNode(iTestResult);
				MainReporting.reportPass(iTestResult);
			}
		}

		LoggerUtil.log("<<<*** END: " + sTestSuiteName + "." + sTestMethodName + " ***>>> ");
		LoggerUtil.log("=====================================================================================");
		LoggerUtil.log("Test Passed :" + testName + ", Took " + timeTaken + " seconds");

	}

	public void beforeInvocation(IInvokedMethod arg0, ITestResult arg1) {
	}

	public void afterInvocation(IInvokedMethod method, ITestResult result) {
		Reporter.setCurrentTestResult(result);

		if (method.isTestMethod()) {

			List<Throwable> verificationFailures = VerifySafe.getVerificationFailures();

			if (verificationFailures.size() > 0) {

				result.setStatus(2);

				if (result.getThrowable() != null) {
					verificationFailures.add(result.getThrowable());
				}

				int size = verificationFailures.size();

				if (size == 1) {
					result.setThrowable((Throwable) verificationFailures.get(0));

				} else {

					StringBuffer failureMessage = (new StringBuffer("Multiple asserts failed (")).append(size)
							.append("):\n");
					for (int i = 0; i < size; i++) {
						failureMessage.append("Assertion ").append(i + 1).append(" of ").append(size).append(":\n");
						Throwable t = (Throwable) verificationFailures.get(i);

						failureMessage.append(t.getMessage()).append("\n");
					}

					Throwable last = (Throwable) verificationFailures.get(size - 1);

					Throwable merged = new Throwable(failureMessage.toString());
					merged.setStackTrace(last.getStackTrace());

					result.setThrowable(merged);
				}
			}
		}
	}

	private void removeIncorrectlyFailedTests(ITestContext test) {
		IResultMap failedwithSuccess = test.getFailedButWithinSuccessPercentageTests();
		int countOffailedwithSuccessResults = test.getSkippedTests().getAllResults().size();
		int countOfPassedResults = test.getPassedTests().getAllResults().size();
		int countOfFailedResults = test.getFailedTests().getAllResults().size();
		int countOfAllResults = test.getAllTestMethods().length;

		if ((countOfAllResults == countOfPassedResults || countOfAllResults == countOfFailedResults)
				&& countOffailedwithSuccessResults != 0) {
			for (ITestNGMethod method : test.getFailedButWithinSuccessPercentageTests().getAllMethods()) {
				failedwithSuccess.removeResult(method);
			}
		}
	}

	public void onFinish(ISuite arg0) {
	}

	public void onStart(ISuite arg0) {
		File folder = new File(arg0.getOutputDirectory());
		File resultDir = new File(folder.getParent());
		File screenshotFolder = new File(folder.getParent() + File.separator + "screenshots");
		if (!screenshotFolder.exists()) {
			screenshotFolder.mkdirs();
		}
		screenshotPath = screenshotFolder.getPath();
		File videoFolder = new File(folder.getParent() + File.separator + "video");
		if (!videoFolder.exists()) {
			videoFolder.mkdirs();
		}

		HtmlReportListener.resultDir = resultDir.getPath();

		if (!arg0.getParallel().equalsIgnoreCase("none") && EnvParameters.EXECUTION_ENV.equals("local")) {
			if (arg0.getParallel().equalsIgnoreCase("methods")) {
				throw new ItJumpStartReportException(
						"parallel=\"methods\" at suite level in testng XML is not supported as test methods are not thread safe");
			}

			runParallel = true;

		}

		if (!runParallel) {
			for (XmlTest test : arg0.getXmlSuite().getTests()) {
				if (test.getParallel().isParallel()) {
					if (test.getParallel() == XmlSuite.ParallelMode.METHODS) {
						throw new ItJumpStartReportException(
								"parallel=\"methods\" at test level in testng XML is not supported as test methods are not thread safe");
					}

					runParallel = true;

					break;
				}
			}
		}
	}

	public void onExecutionStart() {
		FileInputStream in = null;
		try {
			in = new FileInputStream(System.getProperty("user.dir") + File.separator + "config.properties");
			props.load(in);
		} catch (IOException e) {
			System.err.println("Failed to read: config.properties");
		}

		outdir = "";
		if (ARCHIVE_REPORT) {
			String archivePath = "";
			try {
				Method f = TestNG.class.getMethod("setOutputDirectory", new Class[] { String.class });
				DateFormat dtYearFormat = new SimpleDateFormat("yyyy");
				DateFormat dtMonthFormat = new SimpleDateFormat("M");
				String strCurrYear = dtYearFormat.format(new Date());
				String strCurrMonth = dtMonthFormat.format(new Date());

				archivePath = System.getProperty("user.home") + File.separator + "Hawk Automation Test Report"
						+ File.separator + " Results" + File.separator + strCurrYear + File.separator
						+ theMonth(Integer.parseInt(strCurrMonth)) + File.separator;
				archivePath = archivePath + Utils.getCurrentDateTime("ddMMMyy_hhmmss") + "_" + "Hawk";

				try {
					if (!(new File(archivePath)).mkdirs()) {
						throw new ReportConfigException("Failed to create the archive report directory");
					}
				} catch (Exception e) {
					throw new ReportConfigException(e.getMessage(), e);
				}

				outdir = archivePath;
				LoggerUtil.updateLog4jConfiguration(
						archivePath + File.separator + "logs" + File.separator + "report.log");

				Object[] a = { outdir };
				f.invoke(TestNG.getDefault(), a);
			} catch (Exception e) {

				throw new ReportConfigException("Unable to set archive report directory", e);
			}
		} else {
			try {
				Method f = TestNG.class.getMethod("getOutputDirectory", new Class[0]);
				Object[] a = new Object[0];
				outdir = (String) f.invoke(TestNG.getDefault(), a);
			} catch (Exception e) {

				throw new ReportConfigException("Unable to read report directory", e);
			}
			LoggerUtil.updateLog4jConfiguration(outdir + File.separator + "logs" + File.separator + "report.log");
		}
		if (GEN_ADVANCED_REPORT) {
			String strBrowser = EnvParameters.BROWSER_NAME;
			if (outdir.endsWith("test-output") || outdir.endsWith("surefire-reports")) {
				itJumpStart_ReportDir = outdir + File.separator + "Hawk-Report";
			} else {
				if (outdir.equalsIgnoreCase("")) {
					throw new ReportConfigException("Unable to read report directory");
				}
				itJumpStart_ReportDir = outdir + File.separator + "Hawk-Report";
			}

			File outputDirectory = new File(itJumpStart_ReportDir);
			outputDirectory.mkdirs();
			if (outputDirectory.exists()) {
				MainReporting.createInitXML(itJumpStart_ReportDir);
				MainReporting.writeEnvDetailsToXMLReport();
				MainReporting.addTestCaseNode(SUITNAME + " Test Results");
			} else {
				throw new ReportCreationException("Unable to create XML report");
			}
		}
	}

	public void onExecutionFinish() {
		if (GEN_ADVANCED_REPORT) {
			MainReporting.generateHtmlReport();
		}

		try {
			Method f = TestNG.class.getMethod("getOutputDirectory", new Class[0]);
			Object[] a = new Object[0];
			outdir = (String) f.invoke(TestNG.getDefault(), a);
		} catch (Exception e) {

			LoggerUtil.log(e.toString(), Level.WARN);
		}
		LoggerUtil.log("Report Directory: " + outdir.replaceAll("TestNG-Results", ""));

	}

	private static String theMonth(int month) {
		String[] monthNames = { "January", "February", "March", "April", "May", "June", "July", "August", "September",
				"October", "November", "December" };

		return monthNames[month - 1];
	}

	public static String getOutdir() {
		return outdir;
	}

	@Override
	public void onFinish(ITestContext context) {
		// TODO Auto-generated method stub

	}
}
