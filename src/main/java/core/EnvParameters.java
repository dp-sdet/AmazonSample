package core;
/**
 * Setting up environment parameters
 * Can be used for multiple environment like dev,devint,qa
 * 
 */
import exception.CustomException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class EnvParameters {
	//constant files
	private static final String PROP_FILE = "config.properties";
	public static final int TIME_OUT;
	public static final String TEST_ROOT_DIR;
	public static final String WEB_BROWSER;
	public static final boolean CAPTURE_SCREENSHOT;
	public static final String EXECUTION_ENV;
	public static final String DESKTOP_APP;

	public static String BROWSER_NAME = null;
	private static Properties properties = new Properties();
	/*
	 * Receiving and processing the inputs from config.properties
	 */
	static {
		TEST_ROOT_DIR = System.getProperty("user.dir");
		FileInputStream in = null;
		
		/*
		 * check whether config.properties is present or not
		 */
		try {
			in = new FileInputStream(TEST_ROOT_DIR + File.separator + PROP_FILE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new CustomException(
					"config.properties -> Config file not found, Please specify the correct config file");
		}

		/*
		 * to load config.properties
		 */
		try {
			properties.load(in);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new CustomException("Failure loading property file -> " + e.getMessage());
		}
		/*
		 * checking the execution env and loading the same. Can be extended for seetest/saucelabs if required
		 */
		if (System.getProperty("execution.env") != null && !System.getProperty("execution.env").equalsIgnoreCase("")) {
			EXECUTION_ENV = System.getProperty("execution.env");
		} else if (properties.getProperty("execution.env") != null
				&& !properties.getProperty("execution.env").equalsIgnoreCase("")) {
			EXECUTION_ENV = properties.getProperty("execution.env");
		} else {
			throw new CustomException(
					"execution.env property not set, it is mandate to define the execution.env property");
		}
		/*
		 * timeout value check
		 */

		if (System.getProperty("time.out") != null && !System.getProperty("time.out").equalsIgnoreCase("")) {
			TIME_OUT = Integer.parseInt(System.getProperty("time.out"));
		} else if (properties.getProperty("time.out") != null
				&& !properties.getProperty("time.out").equalsIgnoreCase("")) {
			TIME_OUT = Integer.parseInt(properties.getProperty("time.out"));
		} else {
			throw new CustomException("Time out property not set, it is mandate to define the Time out property");
		}
		/*
		 * validating browser property is set/not
		 */

		if (System.getProperty("Browser") != null && !System.getProperty("Browser").equalsIgnoreCase("")) {
			WEB_BROWSER = System.getProperty("Browser");
		} else if (properties.getProperty("Browser") != null
				&& !properties.getProperty("Browser").equalsIgnoreCase("")) {
			WEB_BROWSER = properties.getProperty("Browser");
		} else {
			throw new CustomException("Browser property not set");
		}

		DESKTOP_APP = null;
		/*
		 *Validate whether screenshot to be taken/ not based on the value set in the capture screenshot in config.properties
		 */
		if (System.getProperty("capture.screenshot") != null
				&& !System.getProperty("capture.screenshot").equalsIgnoreCase("")) {
			CAPTURE_SCREENSHOT = Boolean.valueOf(System.getProperty("capture.screenshot")).booleanValue();
		} else if (properties.getProperty("capture.screenshot") != null
				&& !properties.getProperty("capture.screenshot").equalsIgnoreCase("")) {
			CAPTURE_SCREENSHOT = Boolean.valueOf(properties.getProperty("capture.screenshot")).booleanValue();
			System.setProperty("capture.screenshot", properties.getProperty("capture.screenshot"));
		} else {
			throw new CustomException(
					"Screenshot capture is not set. It is mandate to set Screenshot.capture for screenshots");
		}

	}
}
