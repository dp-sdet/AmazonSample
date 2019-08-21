package report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.BeforeSuite;











public abstract class VerifySafe
{
  private static Map<ITestResult, List<Throwable>> verificationFailuresHashMap = new HashMap();

  
  private static final String ESCAPE_PROPERTY = "org.uncommons.reportng.escape-output";


  
  protected void print(String s) { Reporter.log(s, true); }


  
  @BeforeSuite(alwaysRun = true)
  public void setupReport() { System.setProperty("org.uncommons.reportng.escape-output", "false"); }








  

  public static List<Throwable> getVerificationFailures() {
	    List verificationFailures = (List)verificationFailuresHashMap.get(Reporter.getCurrentTestResult());
	    return (verificationFailures == null) ? new ArrayList() : verificationFailures;
	  }




  
  protected static void addVerificationFailure(Throwable e) {
    List<Throwable> verificationFailures = getVerificationFailures();
    verificationFailuresHashMap.put(Reporter.getCurrentTestResult(), verificationFailures);
    
    verificationFailures.add(e);
  }









  
  public static void verifySafely(Object actual, Object expected, String message) {
    try {
      Assert.assertEquals(actual, expected, message);
      LoggerUtil.log("Expected value: " + expected + " Actual value: " + actual + " - PASSED : " + message);
    }
    catch (Throwable e) {
      LoggerUtil.log("Expected value: " + expected + " Actual value: " + actual + " - FAILED : " + message);
      
      addVerificationFailure(e);
    } 
  }











  
  public static void verifyAssert(Object actual, Object expected, String message) {
    try {
      Assert.assertEquals(actual, expected, message);
      LoggerUtil.log("Expected value: " + expected + " Actual value: " + actual + " - PASSED : " + message);
    }
    catch (AssertionError e) {
      LoggerUtil.log("Expected value: " + expected + " Actual value: " + actual + " - FAILED  : " + message);
      
      throw new RuntimeException(e);
    } catch (Exception e) {
      LoggerUtil.log("Expected value: " + expected + " Actual value: " + actual + " - FAILED  : " + message);
      
      throw new RuntimeException(e);
    } 
  }
}
