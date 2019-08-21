package Utils;

import report.LoggerUtil;
import report.VerifySafe;
import core.EnvParameters;
import core.WebBase;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.IInvokedMethod;
import org.testng.IResultMap;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.internal.Utils;

public class CustomListeners implements ITestListener {
	private static final String ESCAPE_PROPERTY = "org.uncommons.reportng.escape-output";
	private String testRoot = EnvParameters.TEST_ROOT_DIR;

	public String sFilename;

	public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {
	}

	public void onFinish(ITestContext context) {

		removeIncorrectlyFailedTests(context);
	}

	public void onStart(ITestContext context) {
		System.setProperty("org.uncommons.reportng.escape-output", "false");

	}

	public void onTestFailure(ITestResult result) {
		String sTestMethodName = result.getMethod().getMethodName();
		String sTestSuiteName = result.getTestClass().getRealClass().getSimpleName();
		String timeTaken = Long.toString((result.getEndMillis() - result.getStartMillis()) / 1000L);
		String testName = result.getTestClass().getName() + "." + result.getMethod().getMethodName();
		LoggerUtil.log("<<<*** END: " + sTestSuiteName + "." + sTestMethodName + " ***>>> ");
		LoggerUtil.log("=====================================================================================");

		LoggerUtil.log("Test Failed :" + testName + ", Took " + timeTaken + " seconds");
		Object currentClass = result.getInstance();
		WebDriver driver = null;
		Class C = result.getInstance().getClass();
		while (C != null) {
			if (C.getName().contains("WebBase")) {
				driver = ((WebBase) currentClass).getDriver();
				break;
			}
			driver = null;

			C = C.getSuperclass();
		}

		if (driver != null) {
			if (EnvParameters.CAPTURE_SCREENSHOT && EnvParameters.EXECUTION_ENV.equals("local")) {
				takeScreenShot(sTestMethodName, driver);
				Reporter.log("<br><a href='../../screenshots/" + result.getMethod().getMethodName()
						+ ".png'><img src='../../screenshots/" + result.getMethod().getMethodName()
						+ ".png' height='100' width='100'/><br></a>");
			}

		}
	}

	public void onTestSkipped(ITestResult iTestResult) {
		WebDriver driver;
		String timeTaken = Long.toString((iTestResult.getEndMillis() - iTestResult.getStartMillis()) / 1000L);

		String testName = iTestResult.getTestClass().getName() + "." + iTestResult.getMethod().getMethodName();
		LoggerUtil.log("/////////////////////////////////////////////////////////////////////////////////////////");

		LoggerUtil.log("Test Skipped :" + testName + ", Took " + timeTaken + " seconds");

		String currentClassName = iTestResult.getInstance().getClass().getSuperclass().getSuperclass().toString();
		Object currentClass = iTestResult.getInstance();

		if (currentClassName.toString().contains("WebBase")) {
			driver = ((WebBase) currentClass).getDriver();
		} else {
			driver = null;
		}
	}

	public void onTestStart(ITestResult iTestResult) {
		String sTestMethodName = iTestResult.getMethod().getMethodName();
		String sTestSuiteName = iTestResult.getTestClass().getRealClass().getSimpleName();
		LoggerUtil
				.log("===============================================================================================");

		LoggerUtil.log("<<<*** START: " + sTestSuiteName + "." + sTestMethodName + " ***>>> ");

		Object currentClass = iTestResult.getInstance();
		WebDriver driver = null;
		Class C = iTestResult.getInstance().getClass();
		while (C != null) {
			if (C.getName().contains("WebBase")) {
				driver = ((WebBase) currentClass).getDriver();
				break;
			}
			driver = null;

			C = C.getSuperclass();
		}

	}

	public void onTestSuccess(ITestResult iTestResult) {
		WebDriver driver;
		String sTestMethodName = iTestResult.getMethod().getMethodName();
		String sTestSuiteName = iTestResult.getTestClass().getRealClass().getSimpleName();

		String timeTaken = Long.toString((iTestResult.getEndMillis() - iTestResult.getStartMillis()) / 1000L);

		String testName = iTestResult.getTestClass().getName() + "." + iTestResult.getMethod().getMethodName();

		LoggerUtil.log("<<<*** END: " + sTestSuiteName + "." + sTestMethodName + " ***>>> ");
		LoggerUtil.log("=====================================================================================");

		LoggerUtil.log("Test Passed :" + testName + ", Took " + timeTaken + " seconds");
		String currentClassName = iTestResult.getInstance().getClass().getSuperclass().toString();
		Object currentClass = iTestResult.getInstance();

		if (currentClassName.toString().contains("WebBase")) {
			driver = ((WebBase) currentClass).getDriver();
		} else {
			driver = null;
		}
	}

	public void takeScreenShot(String methodName, WebDriver driver) {
		if (driver != null) {
			File scrFile = (File) ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

			try {
				FileUtils.copyFile(scrFile,
						new File("target" + File.separator + "screenshots" + File.separator + methodName + ".png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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

					StringBuffer failureMessage = (new StringBuffer("Multiple failures (")).append(size)
							.append("):\n\n");
					for (int i = 0; i < size; i++) {
						failureMessage.append("Failure ").append(i + 1).append(" of ").append(size).append(":\n");
						Throwable t = (Throwable) verificationFailures.get(i);
						String errorMessage = null;
						errorMessage = Utils.stackTrace(t, false)[1];
						failureMessage.append(errorMessage).append("\n\n");
					}

					Throwable merged = new Throwable(failureMessage.toString());

					result.setThrowable(merged);
				}
			}
		}
	}

	private IResultMap removeIncorrectlyFailedTests(ITestContext test) {
		List<ITestNGMethod> failsToRemove = new ArrayList<ITestNGMethod>();
		IResultMap returnValue = test.getFailedTests();
		for (ITestResult result : test.getFailedTests().getAllResults()) {
			long failedResultTime = result.getEndMillis();
			for (ITestResult resultToCheck : test.getFailedButWithinSuccessPercentageTests().getAllResults()) {
				if (failedResultTime == resultToCheck.getEndMillis()) {
					failsToRemove.add(resultToCheck.getMethod());
					break;
				}
			}
			for (ITestResult resultToCheck : test.getPassedTests().getAllResults()) {
				if (failedResultTime == resultToCheck.getEndMillis()) {
					failsToRemove.add(resultToCheck.getMethod());
				}
			}
		}

		for (ITestNGMethod method : failsToRemove) {
			returnValue.removeResult(method);
		}
		return returnValue;
	}
}
