package constant;

import org.testng.SkipException;

import dataReader.TestData;
import core.WebBase;

public class Constant extends WebBase {

	
	public static String URL;

	public void loadurl() {
		URL = TestData.getData("AmazonURL");
	}

	
	protected void skipTestData() {
		throw new SkipException("Runmode set to No");
	}

}
