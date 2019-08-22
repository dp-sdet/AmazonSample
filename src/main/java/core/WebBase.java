package core;

/**
 * Base class for initiating the drivers with its desired capabilities.
 * Most of the methods have been given self-explanatory names for better understanding.
 */

import exception.CustomException;
import report.LoggerUtil;
import Utils.OSUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

public class WebBase {
	protected WebDriver driver;
	protected String browser;
	protected String browserPlatform;
	protected String browserVersion;
	protected String screenResolution;

	protected void log(String message, String level) {
		LoggerUtil.log(message, level);
	}

	protected void log(String message) {
		LoggerUtil.log(message);
	}

	/*
	 * SetUp Method for browser running
	 */
	@Parameters({ "Browser", "BrowserVersion", "BrowserPlatform", "ScreenResolution" })
	@BeforeMethod(alwaysRun = true)
	public void testSetup(@Optional String browser, @Optional String browserVersion, @Optional String browserPlatform,
			@Optional String screenResolution, @Optional Method method) throws Exception {
		if (EnvParameters.EXECUTION_ENV.equals("local")) {
			if (StringUtils.isNotEmpty(browser)) {
				this.browser = browser;
			} else if (StringUtils.isNotEmpty(EnvParameters.WEB_BROWSER)) {
				this.browser = EnvParameters.WEB_BROWSER;
			}
			if (this.browser.equals("chrome")) {
				try {
					setupChromeDriver();
				} catch (Exception e) {
					LoggerUtil.log(e.getMessage(), Level.DEBUG);
				}
			}
			if (this.browser.equals("firefox")) {
				try {
					setupGeckoDriver();
				} catch (Exception e) {
					LoggerUtil.log(e.getMessage(), Level.DEBUG);
				}
			}

		}

		this.driver = buildWebDriver(this.browser, this.browserPlatform, this.browserVersion, this.screenResolution,
				method.getName());

		this.driver.manage().timeouts().implicitlyWait(30L, TimeUnit.SECONDS);

		if (!(this.driver instanceof ChromeDriver)) {

			this.driver.manage().window().maximize();
		}
	}

	public WebDriver getDriver() {
		return this.driver;
	}

	/*
	 * WebDriver is initiated using the below method based on the parameters updated
	 * in config.properties
	 */

	@SuppressWarnings("deprecation")
	public WebDriver buildWebDriver(String browser, String browPlatform, String browVersion, String scrResolution,
			String methodName) throws MalformedURLException {

		if (EnvParameters.EXECUTION_ENV.toLowerCase().trim().toString().equals("local")) {

			if (browser.equals("firefox")) {
				String mimeTypes = "application/zip,application/octet-stream,image/jpeg,application/vnd.ms-outlook,text/html,application/pdf";
				FirefoxProfile firefoxProfile = new FirefoxProfile();
				firefoxProfile.setAcceptUntrustedCertificates(true);
				firefoxProfile.setAssumeUntrustedCertificateIssuer(true);
				firefoxProfile.setPreference("browser.download.folderList", 2);
				firefoxProfile.setPreference("browser.download.dir",
						System.getProperty("user.home") + File.separator + "Downloads" + File.separator);

				firefoxProfile.setPreference("browser.helperApps.neverAsk.saveToDisk", mimeTypes);
				firefoxProfile.setPreference("browser.download.manager.showWhenStarting", false);

				firefoxProfile.setPreference("browser.helperApps.alwaysAsk.force", false);
				firefoxProfile.setPreference("browser.download.manager.closeWhenDone", true);

				FirefoxBinary firefoxBinary = new FirefoxBinary();
				FirefoxOptions firefoxOptions = new FirefoxOptions();
				firefoxOptions.setBinary(firefoxBinary);
				firefoxOptions.setProfile(firefoxProfile);

				this.driver = new FirefoxDriver(firefoxOptions);

			} else if (browser.equals("chrome")) {
				DesiredCapabilities capabilities = DesiredCapabilities.chrome();
				HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
				chromePrefs.put("profile.default_content_settings.popups", Integer.valueOf(0));
				chromePrefs.put("download.default_directory",
						System.getProperty("user.home") + File.separator + "Downloads");
				ChromeOptions options = new ChromeOptions();

				HashMap<String, Object> chromeOptionsMap = new HashMap<String, Object>();
				options.setExperimentalOption("prefs", chromePrefs);
				options.addArguments(new String[] { "--test-type" });
				options.addArguments(new String[] { "--start-maximized" });
				capabilities.setCapability("goog:chromeOptions", chromeOptionsMap);
				capabilities.setCapability("acceptSslCerts", true);
				capabilities.setCapability("goog:chromeOptions", options);

				this.driver = new ChromeDriver(capabilities);

			} else {

				throw new SkipException("Browser " + browser + " not supported by this framework");
			}

		} else {

			throw new SkipException(
					EnvParameters.EXECUTION_ENV + " is a invalid Execution Environment. Please use local or saucelabs");
		}

		return this.driver;
	}

	@AfterMethod(alwaysRun = true)
	public void postTestCase(ITestResult _result) {
		if (this.driver != null) {
			this.driver.quit();
		}
	}

	public void deleteAllCookies() {
		this.driver.manage().deleteAllCookies();
	}

	@AfterSuite(alwaysRun = true)
	public void tearDown() {

		if (EnvParameters.WEB_BROWSER.equals("chrome")) {
			killChromeDriver();
		}
		if (EnvParameters.WEB_BROWSER.equals("firefox")) {
			killGeckoDriver();
		}

	}

	private static void killChromeDriver() {
		String _processName = "chromedriver.exe";
		if (OSUtils.isProcessRuning(_processName) == true) {
			OSUtils.killProcess(_processName);
		}
	}

	private static void killGeckoDriver() {
		String _processName = "geckodriver.exe";
		if (OSUtils.isProcessRuning(_processName) == true) {
			OSUtils.killProcess(_processName);
		}
	}

	private static void setupGeckoDriver() throws FileNotFoundException, IOException {
		String GeckoProp = "webdriver.gecko.driver";
		new EnvParameters();
		File targetGeckodriver = null;
		if (OSUtils.getOSname() == OSUtils.OSType.windows) {
			targetGeckodriver = new File(
					EnvParameters.TEST_ROOT_DIR + File.separator + "drivers" + File.separator + "geckodriver.exe");

		}

		if (targetGeckodriver.exists()) {

			System.setProperty(GeckoProp, targetGeckodriver.getAbsolutePath());

			return;
		}
		InputStream reader = null;
		if (OSUtils.getOSname() == OSUtils.OSType.windows) {
			reader = OSUtils.class.getResourceAsStream("/drivers/gecko/win/geckodriver.exe");
		} else {
			LoggerUtil.log("The gecko driver copying is not successfull");
		}

		if (reader.available() > 0) {
			(new File(targetGeckodriver.getParent())).mkdirs();
			FileOutputStream writer = new FileOutputStream(targetGeckodriver);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = reader.read(buffer)) > 0) {
				writer.write(buffer, 0, length);
			}

			writer.close();
			reader.close();
			targetGeckodriver.setExecutable(true, false);
			System.setProperty(GeckoProp, targetGeckodriver.getAbsolutePath());
		} else {
			LoggerUtil.log("Cannot find geckodriver in the jar");
			throw new CustomException("Framework is missing geckodriver file required for Firefox browser.");
		}
	}

	private static void setupChromeDriver() throws FileNotFoundException, IOException {
		new EnvParameters();
		File targetChromedriver = null;
		if (OSUtils.getOSname() == OSUtils.OSType.windows) {
			targetChromedriver = new File(
					EnvParameters.TEST_ROOT_DIR + File.separator + "drivers" + File.separator + "chromedriver.exe");
			System.out.println(targetChromedriver);

		}

		if (targetChromedriver.exists()) {

			System.setProperty("webdriver.chrome.driver", targetChromedriver.getAbsolutePath());

			return;
		}
		InputStream reader = null;
		if (OSUtils.getOSname() == OSUtils.OSType.windows) {
			reader = OSUtils.class.getResourceAsStream("/drivers/chromedriver.exe");
		} else {
			LoggerUtil.log("The chrome driver copying is not successfull");
		}

		if (reader.available() > 0) {
			(new File(targetChromedriver.getParent())).mkdirs();
			FileOutputStream writer = new FileOutputStream(targetChromedriver);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = reader.read(buffer)) > 0) {
				writer.write(buffer, 0, length);
			}

			writer.close();
			reader.close();
			targetChromedriver.setExecutable(true, false);
			System.setProperty("webdriver.chrome.driver", targetChromedriver.getAbsolutePath());
		} else {
			LoggerUtil.log("Cannot find chromedriver in the jar");
			throw new CustomException("Framework is missing chromedriver file required for Chrome browser");
		}
	}

}
