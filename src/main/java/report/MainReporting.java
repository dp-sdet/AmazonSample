package report;

import exception.ReportCreationException;
import Utils.Utils;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.Writer;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.log4j.Level;
import org.jdom2.CDATA;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.testng.ITestResult;










public class MainReporting
{
  private static String strReportFilePath = "";
  private static String strReportFolderPath = "";
  
  private static boolean blnReportRowIDFlag;
  
  private static String strTestRowNumber;
  
  public static final int PASSED = 1;
  
  public static final int FAILED = 0;
  public static final int SKIPPED = 2;
  private static final String[] CSSRESOURCES = { "lightbox.min.css", "Report.css", "jquery-ui-1.8.16.custom.css" };
  
  private static final String[] IMGRESOURCES = { 
      "error_bg.png", "error_sign.png", "False.ico", "FirstDown.ico", "loader.gif", "Logo.jpeg", "HawkLogo.png", "Minus.ico", "panorama.png", "Plus.ico", "skip.ico", "True.ico", "ui-bg_diagonals-thick_18_b81900_40x40.png", "ui-bg_diagonals-thick_20_666666_40x40.png", "ui-bg_flat_10_000000_40x100.png", "ui-bg_glass_100_f6f6f6_1x400.png", "ui-bg_glass_100_fdf5ce_1x400.png", "ui-bg_glass_65_ffffff_1x400.png", "ui-bg_gloss-wave_35_f6a828_500x100.png", "ui-bg_highlight-soft_100_eeeeee_1x100.png", "ui-bg_highlight-soft_75_ffe45c_1x100.png", "ui-icons_222222_256x240.png", "ui-icons_228ef1_256x240.png", "ui-icons_ef8c08_256x240.png", "ui-icons_ffd27a_256x240.png", "ui-icons_ffffff_256x240.png" };








  
  private static final String[] JSRESOURCES = { "Chart.min.js", "jquery-1.6.4.min.js", "jquery.ui.core.min.js", "jquery.ui.rlightbox.min.js", "jquery.ui.widget.min.js" };


  
  private static String limitChars(String strValueToBeLimited, int intNoChar) {
    String strLimitedValue = "";
    if (strValueToBeLimited.length() < intNoChar) {
      strLimitedValue = strValueToBeLimited;
    } else {
      strLimitedValue = strValueToBeLimited.substring(0, intNoChar - 1);
    } 
    return strLimitedValue;
  }
  
  public static void setStrReportFolderPath(String strReportFolderPath) {
    MainReporting.strReportFolderPath = strReportFolderPath;
    strReportFilePath = strReportFolderPath + File.separator + "TestReport.xml";
  }

  
  public static String getStrReportFolderPath() { return strReportFolderPath; }


  
  public static String getStrReportFilePath() { return strReportFilePath; }

  
  public static void createInitXML(String strReportFolderPath) {
    setStrReportFolderPath(strReportFolderPath);
    try {
      String xmlFile = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
      xmlFile = xmlFile + "<?xml-stylesheet href='Report11.xsl' type='text/xsl'?>\n";
      xmlFile = xmlFile + "\n<Report LastXMLNum=\"1\">" + "\n</Report>";
      
      SAXBuilder builder = new SAXBuilder();
      Document doc = builder.build(new StringReader(xmlFile));
      
      writeToXMLFile(doc);
    } catch (Exception e) {
      throw new ReportCreationException("Unable to create the report file", e);
    } 
  }

  
  private static void writeToXMLFile(Document doc) throws IOException {
    Writer writer = new FileWriter(new File(strReportFilePath));
    XMLOutputter xmlOutput = new XMLOutputter(Format.getRawFormat());
    xmlOutput.setFormat(Format.getPrettyFormat().setEncoding("UTF-8"));
    xmlOutput.output(doc, writer);
  }
  
  private static Document getXMLDocument() throws JDOMException, IOException {
    SAXBuilder builder = new SAXBuilder();
    File xmlFile = new File(strReportFilePath);
    if (!xmlFile.exists()) {
      throw new ReportCreationException("Unable to locate the report file");
    }
    Object doc = null;
    return builder.build(xmlFile);
  }




  
  public static void writeEnvDetailsToXMLReport() {
    try {
      Document doc = getXMLDocument();
      Element rootElement = doc.getRootElement();

      
      rootElement.setAttribute("ApplicationName", GenericListener.APPLICATION_NAME);
      rootElement.setAttribute("Env", GenericListener.ENVIRONMENT);
      rootElement.setAttribute("RunFlow", GenericListener.SUITNAME);
      rootElement.setAttribute("BuildVersion", GenericListener.BUILDNAME);
      try {
        rootElement.setAttribute("Host", InetAddress.getLocalHost().getHostName());
      } catch (UnknownHostException e) {
        rootElement.setAttribute("Host", "Remote Host");
      } 
      rootElement.setAttribute("User", System.getProperty("user.name"));
      rootElement.setAttribute("XMLNum", String.valueOf(1));
      rootElement.setAttribute("DateTime", Utils.getCurrentDateTime("dd-MMM-yyyy hh:mm:ss.SSS"));
      
      rootElement.setAttribute("PrevXMLFileName", "");
      rootElement.setAttribute("NextXMLFileName", "");



      
      try {
        if (System.getProperty("capture.screenshot").equals("true") || 
          System.getProperty("capture.screenshot").equals("true")) {
          rootElement.setAttribute("Screenshot", System.getProperty("capture.screenshot").toString());
          rootElement.setAttribute("Video", System.getProperty("capture.screenshot").toString());
        }
      
      } catch (Exception e) {
        rootElement.setAttribute("Screenshot", "Turned Off");
        rootElement.setAttribute("Video", "Turned off");
      } 
      
      writeToXMLFile(doc);
    }
    catch (Exception e) {
      throw new ReportCreationException("Unable to write Env details to report", e);
    } 
  }





  
  public static void addTestCaseNode(String strTestCaseID) {
    try {
      blnReportRowIDFlag = true;
      strTestRowNumber = "1";
      Document doc = getXMLDocument();
      Element rootNode = doc.getRootElement();
      
      Element childTestCase = new Element("TestCase");

      
      List<Element> lstTestCase = rootNode.getChildren("TestCase");
      
      int newTestCaseID = lstTestCase.size() + 1;
      childTestCase.setAttribute("ID", String.valueOf(newTestCaseID));
      childTestCase.setAttribute("Desc", strTestCaseID);
      childTestCase.setAttribute("TCStatus", String.valueOf(2));
      childTestCase.setAttribute("Row", strTestRowNumber);





      
      rootNode.addContent(childTestCase);
      
      writeToXMLFile(doc);
    }
    catch (Exception e) {
      throw new ReportCreationException("Unable to add test ", e);
    } 
  }








  
  private static void addTestActivityNode(String strDescription) throws JDOMException, IOException {
    Document doc = getXMLDocument();
    List<Element> lstTestCase = doc.getRootElement().getChildren("TestCase");
    
    Element currentTestCase = (Element)lstTestCase.get(lstTestCase.size() - 1);

    
    Element childTestActivity = new Element("TestActivity");

    
    List<Element> lstTestActivity = currentTestCase.getChildren("TestActivity");
    boolean testActivityElement = false;
    for (Element activity : lstTestActivity) {
      if (activity.getAttributeValue("Desc").equalsIgnoreCase(strDescription)) {
        testActivityElement = true;
      }
    } 
    if (!testActivityElement) {
      int newTestActivityID = lstTestActivity.size() + 1;
      childTestActivity.setAttribute("ID", String.valueOf(newTestActivityID));
      childTestActivity.setAttribute("Desc", strDescription);
      currentTestCase.addContent(childTestActivity);
      writeToXMLFile(doc);
    } 
  }






  
  public static void addTestMethodNode(ITestResult result) {
    String browser = result.getMethod().getXmlTest().getParameter("Browser");
    String strTestMethod = "";
    if (browser == null || browser.equalsIgnoreCase("")) {
      strTestMethod = result.getMethod().getMethodName();
    } else {
      strTestMethod = result.getMethod().getMethodName() + browser;
    } 
    
    String sTestClassName = result.getTestClass().getRealClass().getCanonicalName();
    String strIteration = String.valueOf(result.getMethod().getCurrentInvocationCount());
    String sTestXMLName = result.getMethod().getXmlTest().getName();
    String strDescription = strTestMethod;

    
    try {
      addTestActivityNode(sTestXMLName + " - " + sTestClassName);
      
      Document doc = getXMLDocument();
      
      List<Element> lstTestCase = doc.getRootElement().getChildren("TestCase");
      Element currentTestCase = (Element)lstTestCase.get(lstTestCase.size() - 1);
      List<Element> lstTestActivity = currentTestCase.getChildren("TestActivity");
      Element currentTestActivity = null;
      for (Element activity : lstTestActivity) {
        if (activity.getAttributeValue("Desc")
          .equalsIgnoreCase(sTestXMLName + " - " + sTestClassName)) {
          currentTestActivity = activity;
        }
      } 
      if (currentTestActivity != null) {
        List<Element> lstTestMethod = currentTestActivity.getChildren("TestMethod");
        Element currentTestMethod = getTestMethodNode(lstTestMethod, strTestMethod, strIteration);
        
        if (currentTestMethod == null) {
          Element childTestMethod = new Element("TestMethod");
          
          lstTestMethod = currentTestActivity.getChildren("TestMethod");
          int newTestMethodID = lstTestMethod.size() + 1;
          childTestMethod.setAttribute("ID", String.valueOf(newTestMethodID));
          childTestMethod.setAttribute("Desc", strDescription);
          childTestMethod.setAttribute("Iter", strIteration);
          
          currentTestActivity.addContent(childTestMethod);
          XMLOutputter xmlOutput = new XMLOutputter(Format.getRawFormat());
          xmlOutput.setFormat(Format.getPrettyFormat().setEncoding("US-ASCII"));
          xmlOutput.output(doc, new FileWriter(strReportFilePath));
        } 
      } else {
        throw new ReportCreationException("Failed to create the TestActivity Node");
      } 
    } catch (Exception e) {
      throw new ReportCreationException("Failed to create test method node", e);
    } 
  }








  
  public static boolean addResultNode(ITestResult result, int intStatus) {
    String browser = result.getMethod().getXmlTest().getParameter("Browser");
    String strTestMethod = "";
    if (browser == null || browser.equalsIgnoreCase("")) {
      strTestMethod = result.getMethod().getMethodName();
    } else {
      strTestMethod = result.getMethod().getMethodName() + browser;
    } 
    
    String strDuration = Long.toString((result.getEndMillis() - result.getStartMillis()) / 1000L);
    String testName = result.getTestClass().getName() + "." + result.getMethod().getMethodName();
    String strIteration = String.valueOf(result.getMethod().getCurrentInvocationCount());
    String sTestClassName = result.getTestClass().getRealClass().getCanonicalName();
    
    String strDescription = "";
    if (intStatus == 1)
      strDescription = "Test Passed: " + testName; 
    if (intStatus == 0)
    {
      strDescription = "Test Failed: " + testName + " ; Exception Occured:" + result.getThrowable().toString(); } 
    if (intStatus == 2)
    {
      strDescription = "Test Skipped: " + testName + ((result.getThrowable() != null) ? (" ; Exception Occured:" + result.getThrowable().toString()) : ""); } 
    String sTestXMLName = result.getMethod().getXmlTest().getName();
    
    try {
      strTestRowNumber = "1";
      
      Document doc = getXMLDocument();
      
      List<Element> lstTestCase = doc.getRootElement().getChildren("TestCase");
      Element currentTestCase = (Element)lstTestCase.get(lstTestCase.size() - 1);
      List<Element> lstTestActivity = currentTestCase.getChildren("TestActivity");
      
      Element currentTestActivity = null;
      for (Element activity : lstTestActivity) {
        if (activity.getAttributeValue("Desc")
          .equalsIgnoreCase(sTestXMLName + " - " + sTestClassName)) {
          currentTestActivity = activity;
        }
      } 
      
      List<Element> lstTestMethod = currentTestActivity.getChildren("TestMethod");

      
      Element currentTestMethod = getTestMethodNode(lstTestMethod, strTestMethod, strIteration);

      
      Element childMethodResult = new Element("MethodResult");

      
      childMethodResult.setAttribute("Status", String.valueOf(intStatus));
      childMethodResult.setAttribute("Time", String.valueOf(new Date()));

      
      if (blnReportRowIDFlag && intStatus % 10 == 0) {
        childMethodResult.setAttribute("ID", "fail_" + strTestRowNumber);
        blnReportRowIDFlag = false;
      } 
      childMethodResult.setAttribute("duration", strDuration);
      childMethodResult.setText(strDescription);
      
      currentTestMethod.addContent(childMethodResult);
      
      if (intStatus == 0) {
        Element stacktrace = createStackTraceElement(result);
        currentTestMethod.addContent(stacktrace);
      } 
      if (intStatus == 2) {
        for (ITestResult result1 : result.getTestContext().getFailedConfigurations()
          .getAllResults()) {
          Element stacktrace = createStackTraceElement(result1);
          stacktrace.setAttribute("configMethod", "Failed Config Method: " + result1
              .getInstanceName() + "." + result1.getName());
          currentTestMethod.addContent(stacktrace);
        } 
        if (result.getThrowable() != null) {
          Element stacktrace = createStackTraceElement(result);
          currentTestMethod.addContent(stacktrace);
        } 
      } 


      
      if (intStatus == 0 || intStatus == 20) {
        if (!currentTestCase.getAttributeValue("TCStatus").equals("10")) {
          currentTestCase.setAttribute("TCStatus", "0");
        } else {
          currentTestCase.setAttribute("TCStatus", "10");
        }
      
      } else if (!currentTestCase.getAttributeValue("TCStatus").equals("0") && 
        !currentTestCase.getAttributeValue("TCStatus").equals("10")) {
        if (!currentTestCase.getAttributeValue("TCStatus").equals("11")) {
          currentTestCase.setAttribute("TCStatus", "1");
        } else {
          currentTestCase.setAttribute("TCStatus", "11");
        } 
      } 

      
      XMLOutputter xmlOutput = new XMLOutputter(Format.getRawFormat());
      xmlOutput.setFormat(Format.getPrettyFormat().setEncoding("US-ASCII"));
      xmlOutput.output(doc, new FileWriter(strReportFilePath));
      return true;
    } catch (IndexOutOfBoundsException e) {


      
      LoggerUtil.log(e.getMessage(), Level.DEBUG);
      return false;
    } catch (Exception e) {
      LoggerUtil.log(e.getMessage(), Level.DEBUG);
      return false;
    } 
  }

  
  public static void addResultInfoNode(ITestResult result, String strDescription, int intStatus) {
    String browser = result.getMethod().getXmlTest().getParameter("Browser");
    String strTestMethod = "";
    if (browser == null || browser.equalsIgnoreCase("")) {
      strTestMethod = result.getMethod().getMethodName();
    } else {
      strTestMethod = result.getMethod().getMethodName() + browser;
    } 




    
    String strIteration = String.valueOf(result.getMethod().getCurrentInvocationCount());
    String sTestClassName = result.getTestClass().getRealClass().getCanonicalName();
    
    String sTestXMLName = result.getMethod().getXmlTest().getName();

    
    try {
      strTestRowNumber = "1";
      
      Document doc = getXMLDocument();
      List<Element> lstTestCase = doc.getRootElement().getChildren("TestCase");
      Element currentTestCase = (Element)lstTestCase.get(lstTestCase.size() - 1);
      List<Element> lstTestActivity = currentTestCase.getChildren("TestActivity");
      
      Element currentTestActivity = null;
      for (Element activity : lstTestActivity) {
        if (activity.getAttributeValue("Desc")
          .equalsIgnoreCase(sTestXMLName + " - " + sTestClassName)) {
          currentTestActivity = activity;
        }
      } 
      
      List<Element> lstTestMethod = currentTestActivity.getChildren("TestMethod");

      
      Element currentTestMethod = getTestMethodNode(lstTestMethod, strTestMethod, strIteration);

      
      Element childMethodResult = new Element("MethodResult");

      
      childMethodResult.setAttribute("Status", String.valueOf(intStatus));
      childMethodResult.setAttribute("Time", String.valueOf(new Date()));

      
      if (blnReportRowIDFlag && intStatus % 10 == 0) {
        childMethodResult.setAttribute("ID", "fail_" + strTestRowNumber);
        blnReportRowIDFlag = false;
      } 
      childMethodResult.setText(strDescription);

      
      currentTestMethod.addContent(childMethodResult);
      
      XMLOutputter xmlOutput = new XMLOutputter(Format.getRawFormat());
      xmlOutput.setFormat(Format.getPrettyFormat().setEncoding("US-ASCII"));
      xmlOutput.output(doc, new FileWriter(strReportFilePath));
    } catch (Exception e) {
      throw new ReportCreationException("Unable to add Result info node", e);
    } 
  }

  
  private static Element getTestMethodNode(List<Element> lstTestMethod, String strDescription, String strIteration) {
    for (Element method : lstTestMethod) {
      if (method.getAttributeValue("Desc").equalsIgnoreCase(strDescription) && method
        .getAttributeValue("Iter").equalsIgnoreCase(strIteration)) {
        return method;
      }
    } 
    return null;
  }






  
  public static void reportPass(ITestResult result) {
    String browser = result.getMethod().getXmlTest().getParameter("Browser");
    String strTestMethod = "";
    if (browser == null || browser.equalsIgnoreCase("")) {
      strTestMethod = result.getMethod().getMethodName();
    } else {
      strTestMethod = result.getMethod().getMethodName() + browser;
    } 
    
    String strDuration = Long.toString((result.getEndMillis() - result.getStartMillis()) / 1000L);
    String testName = result.getTestClass().getName() + "." + result.getMethod().getMethodName();
    String strIteration = String.valueOf(result.getMethod().getCurrentInvocationCount());
    String sTestClassName = result.getTestClass().getRealClass().getCanonicalName();
    
    String strDescription = "Test Passed: " + testName;
    String sTestXMLName = result.getMethod().getXmlTest().getName();
    
    try {
      strTestRowNumber = "1";
      
      Document doc = getXMLDocument();
      
      List<Element> lstTestCase = doc.getRootElement().getChildren("TestCase");
      Element currentTestCase = (Element)lstTestCase.get(lstTestCase.size() - 1);
      List<Element> lstTestActivity = currentTestCase.getChildren("TestActivity");
      
      Element currentTestActivity = null;
      for (Element activity : lstTestActivity) {
        if (activity.getAttributeValue("Desc")
          .equalsIgnoreCase(sTestXMLName + " - " + sTestClassName)) {
          currentTestActivity = activity;
        }
      } 
      
      List<Element> lstTestMethod = currentTestActivity.getChildren("TestMethod");

      
      Element currentTestMethod = getTestMethodNode(lstTestMethod, strTestMethod, strIteration);

      
      Element childMethodResult = new Element("MethodResult");

      
      childMethodResult.setAttribute("Status", String.valueOf(1));
      childMethodResult.setAttribute("Time", String.valueOf(new Date()));
      
      childMethodResult.setAttribute("duration", strDuration);
      childMethodResult.setText(strDescription);
      
      currentTestMethod.addContent(childMethodResult);

      
      if (Integer.parseInt(currentTestCase.getAttributeValue("TCStatus")) == 2) {
        currentTestCase.setAttribute("TCStatus", String.valueOf(1));
      }
      
      XMLOutputter xmlOutput = new XMLOutputter(Format.getRawFormat());
      xmlOutput.setFormat(Format.getPrettyFormat().setEncoding("US-ASCII"));
      xmlOutput.output(doc, new FileWriter(strReportFilePath));
    } catch (Exception e) {
      throw new ReportCreationException("Unable to update test result", e);
    } 
  }






  
  public static void reportError(ITestResult result) {
    String browser = result.getMethod().getXmlTest().getParameter("Browser");
    String strTestMethod = "";
    if (browser == null || browser.equalsIgnoreCase("")) {
      strTestMethod = result.getMethod().getMethodName();
    } else {
      strTestMethod = result.getMethod().getMethodName() + browser;
    } 
    
    String strDuration = Long.toString((result.getEndMillis() - result.getStartMillis()) / 1000L);
    String testName = result.getTestClass().getName() + "." + result.getMethod().getMethodName();
    String strIteration = String.valueOf(result.getMethod().getCurrentInvocationCount());
    String sTestClassName = result.getTestClass().getRealClass().getCanonicalName();

    
    String strDescription = "Test Failed: " + testName + " ; Exception Occured:" + result.getThrowable().toString();
    String sTestXMLName = result.getMethod().getXmlTest().getName();
    
    try {
      strTestRowNumber = "1";
      
      Document doc = getXMLDocument();
      
      List<Element> lstTestCase = doc.getRootElement().getChildren("TestCase");
      Element currentTestCase = (Element)lstTestCase.get(lstTestCase.size() - 1);
      List<Element> lstTestActivity = currentTestCase.getChildren("TestActivity");
      
      Element currentTestActivity = null;
      for (Element activity : lstTestActivity) {
        if (activity.getAttributeValue("Desc")
          .equalsIgnoreCase(sTestXMLName + " - " + sTestClassName)) {
          currentTestActivity = activity;
        }
      } 
      
      List<Element> lstTestMethod = currentTestActivity.getChildren("TestMethod");

      
      Element currentTestMethod = getTestMethodNode(lstTestMethod, strTestMethod, strIteration);

      
      Element childMethodResult = new Element("MethodResult");

      
      childMethodResult.setAttribute("Status", String.valueOf(0));
      childMethodResult.setAttribute("Time", String.valueOf(new Date()));

      
      if (blnReportRowIDFlag) {
        childMethodResult.setAttribute("ID", "fail_" + strTestRowNumber);
        blnReportRowIDFlag = false;
      } 
      childMethodResult.setAttribute("duration", strDuration);
      try {
        if (System.getProperty("capture.screenshot").equals("true") || System.getProperty("capture.video").equals("true")) {
          ResultContainer rc = (ResultContainer)result.getAttribute("resultcontainer");
          if (null != rc) {
            if (rc.getScreeshotfile() != null && System.getProperty("capture.screenshot").equals("true")) {
              childMethodResult.setAttribute("ImagePath", Utils.getRelativePath(rc.getScreeshotfile(), strReportFolderPath));
            }
            if (rc.getWebpagefile() != null) {
              childMethodResult.setAttribute("WebPagePath", Utils.getRelativePath(rc.getWebpagefile(), strReportFolderPath));
            }
            if (rc.getVideofile() != null && !rc.getVideofile().contains("null") && System.getProperty("capture.video").equals("true")) {
              childMethodResult.setAttribute("VideoRecording", Utils.getRelativePath(rc.getVideofile(), strReportFolderPath));
            }
          }
        
        } 
      } catch (Exception e) {}

      
      childMethodResult.setText(strDescription);
      currentTestMethod.addContent(childMethodResult);
      
      Element stacktrace = createStackTraceElement(result);
      currentTestMethod.addContent(stacktrace);

      
      currentTestCase.setAttribute("TCStatus", String.valueOf(0));
      
      XMLOutputter xmlOutput = new XMLOutputter(Format.getRawFormat());
      xmlOutput.setFormat(Format.getPrettyFormat().setEncoding("US-ASCII"));
      xmlOutput.output(doc, new FileWriter(strReportFilePath));
    } catch (Exception e) {
      throw new ReportCreationException("Unable to update test result", e);
    } 
  }
  
  public static void reportWarning(ITestResult result) {
    String browser = result.getMethod().getXmlTest().getParameter("Browser");
    String strTestMethod = "";
    if (browser == null || browser.equalsIgnoreCase("")) {
      strTestMethod = result.getMethod().getMethodName();
    } else {
      strTestMethod = result.getMethod().getMethodName() + browser;
    } 
    
    String strDuration = Long.toString((result.getEndMillis() - result.getStartMillis()) / 1000L);
    String testName = result.getTestClass().getName() + "." + result.getMethod().getMethodName();
    String strIteration = String.valueOf(result.getMethod().getCurrentInvocationCount());
    String sTestClassName = result.getTestClass().getRealClass().getCanonicalName();

    
    String strDescription = "Test Skipped: " + testName + ((result.getThrowable() != null) ? (" ; Exception Occured:" + result.getThrowable().toString()) : "");
    String sTestXMLName = result.getMethod().getXmlTest().getName();
    
    try {
      strTestRowNumber = "1";
      
      Document doc = getXMLDocument();
      
      List<Element> lstTestCase = doc.getRootElement().getChildren("TestCase");
      Element currentTestCase = (Element)lstTestCase.get(lstTestCase.size() - 1);
      List<Element> lstTestActivity = currentTestCase.getChildren("TestActivity");
      
      Element currentTestActivity = null;
      for (Element activity : lstTestActivity) {
        if (activity.getAttributeValue("Desc")
          .equalsIgnoreCase(sTestXMLName + " - " + sTestClassName)) {
          currentTestActivity = activity;
        }
      } 
      
      List<Element> lstTestMethod = currentTestActivity.getChildren("TestMethod");

      
      Element currentTestMethod = getTestMethodNode(lstTestMethod, strTestMethod, strIteration);

      
      Element childMethodResult = new Element("MethodResult");

      
      childMethodResult.setAttribute("Status", String.valueOf(2));
      childMethodResult.setAttribute("Time", String.valueOf(new Date()));
      childMethodResult.setAttribute("duration", strDuration);
      childMethodResult.setText(strDescription);
      
      currentTestMethod.addContent(childMethodResult);
      
      for (ITestResult result1 : result.getTestContext().getFailedConfigurations()
        .getAllResults()) {
        Element stacktrace = createStackTraceElement(result1);
        stacktrace.setAttribute("configMethod", "Failed Config Method: " + result1
            .getInstanceName() + "." + result1.getName());
        currentTestMethod.addContent(stacktrace);
      } 
      if (result.getThrowable() != null) {
        Element stacktrace = createStackTraceElement(result);
        currentTestMethod.addContent(stacktrace);
      } 
      XMLOutputter xmlOutput = new XMLOutputter(Format.getRawFormat());
      xmlOutput.setFormat(Format.getPrettyFormat().setEncoding("US-ASCII"));
      xmlOutput.output(doc, new FileWriter(strReportFilePath));
    } catch (Exception e) {
      throw new ReportCreationException("Unable to update test result", e);
    } 
  }



  
  public static void generateHtmlReport() {
     String outputFileName = "";
    
    try {
      for (String fileName : CSSRESOURCES) {
        String cssfolder = getStrReportFolderPath() + File.separator + "css";
        InputStream is = MainReporting.class.getResourceAsStream("/reporting/css/" + fileName);
        if (is == null) {
          throw new AssertionError("Couldn't find resource: " + fileName);
        }
        copyFile(is, new File(cssfolder, fileName));
      } 
      for (String fileName : IMGRESOURCES) {
        String cssfolder = getStrReportFolderPath() + File.separator + "img";
        InputStream is = MainReporting.class.getResourceAsStream("/reporting/img/" + fileName);
        if (is == null) {
          throw new AssertionError("Couldn't find resource: " + fileName);
        }
        copyFile(is, new File(cssfolder, fileName));
      } 
      for (String fileName : JSRESOURCES) {
        String cssfolder = getStrReportFolderPath() + File.separator + "js";
        InputStream is = MainReporting.class.getResourceAsStream("/reporting/js/" + fileName);
        if (is == null) {
          throw new AssertionError("Couldn't find resource: " + fileName);
        }
        copyFile(is, new File(cssfolder, fileName));
      } 
    } catch (IOException e) {
      
      LoggerUtil.log(e.getMessage(), Level.DEBUG);
    } 
    try {
      TransformerFactory tFactory = TransformerFactory.newInstance();
      InputStream is = MainReporting.class.getResourceAsStream("/reporting/Report.xsl");
      Source xslDoc = new StreamSource(is);
      Source xmlDoc = new StreamSource(strReportFilePath);
      outputFileName = strReportFilePath.replaceAll("xml", "html");
      OutputStream htmlFile = new FileOutputStream(outputFileName);
      Transformer trasform = tFactory.newTransformer(xslDoc);
      trasform.transform(xmlDoc, new StreamResult(htmlFile));
      htmlFile.close();
    } catch (Exception e) {
      
      LoggerUtil.log(e.getMessage(), Level.DEBUG);
      return;
    } 
    try {
      if (GenericListener.OPENREPORT) {
        File htmlFile = new File(outputFileName);
        URI uri = htmlFile.toURI();
        
        try {
          Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null; if (desktop != null && desktop
            .isSupported(Desktop.Action.BROWSE)) desktop.browse(uri);
        
        } catch (Exception e) {
          _executeCmd("chrome", uri.toString());
        } 
      } 
    } catch (Exception e) {
      
      LoggerUtil.log(e.getMessage(), Level.DEBUG);
    } 
  }



  
  public static void _executeCmd(String browserPath, String theUrl) {
    String cmdLine = null;
    String osName = System.getProperty("os.name");
    
    if (osName.startsWith("Windows")) {
      cmdLine = "start " + theUrl;



      
      cmdLine = "rundll32 SHELL32.DLL,ShellExec_RunDLL " + browserPath + " " + theUrl;
    } else if (osName.startsWith("Mac")) {
      cmdLine = "open " + theUrl;
    } else {
      
      cmdLine = "open " + browserPath + " " + theUrl;
    } 
    try {
      Runtime.getRuntime().exec(cmdLine);
    } catch (Exception e) {}
  }








  
  public static List<Throwable> getCauses(Throwable t) {
    List<Throwable> causes = new LinkedList<Throwable>();
    Throwable next = t;
    while (next.getCause() != null) {
      next = next.getCause();
      causes.add(next);
    } 
    return causes;
  }








  
  public static String escapeString(String s) {
    if (s == null) {
      return null;
    }
    
    StringBuilder buffer = new StringBuilder();
    for (int i = 0; i < s.length(); i++) {
      buffer.append(escapeChar(s.charAt(i)));
    }
    return buffer.toString();
  }








  
  private static String escapeChar(char character) {
    switch (character) {
      case '<':
        return "&lt;";
      case '>':
        return "&gt;";
      case '"':
        return "&quot;";
      case '\'':
        return "&apos;";
      case '&':
        return "&amp;";
    } 
    return String.valueOf(character);
  }








  
  public static String escapeHTMLString(String s) {
    if (s == null) {
      return null;
    }
    
    StringBuilder buffer = new StringBuilder();
    for (int i = 0; i < s.length(); i++) {
      char nextCh, ch = s.charAt(i);
      switch (ch) {




        
        case ' ':
          nextCh = (i + 1 < s.length()) ? s.charAt(i + 1) : 0;
          buffer.append((nextCh == ' ') ? "&nbsp;" : " ");
          break;
        case '\n':
          buffer.append("<br/>\n");
          break;
        default:
          buffer.append(escapeChar(ch)); break;
      } 
    } 
    return buffer.toString();
  }
  
  private static Element createStackTraceElement(ITestResult result) {
    Element stacktrace = new Element("stacktrace");
    StringBuffer htmlsec = new StringBuffer("");
    htmlsec.append("<span class=\"exception\">");
    htmlsec.append(escapeHTMLString(result.getThrowable().toString()));
    htmlsec.append("</span><br/>");
    for (StackTraceElement stack : result.getThrowable().getStackTrace()) {
      htmlsec.append(escapeHTMLString(stack.toString()));
      htmlsec.append("<br/>");
    } 
    for (Throwable t : getCauses(result.getThrowable())) {
      htmlsec.append("<span class=\"exception\">Caused By:" + escapeHTMLString(t.toString()));
      htmlsec.append("</span><br/>");
      for (StackTraceElement stack : t.getStackTrace()) {
        htmlsec.append(escapeHTMLString(stack.toString()));
        htmlsec.append("<br/>");
      } 
    } 
    CDATA cdata = new CDATA(htmlsec.toString());
    stacktrace.addContent(cdata);
    return stacktrace;
  }
  
  public static void copyFile(InputStream from, File to) throws IOException {
    if (!to.getParentFile().exists()) {
      to.getParentFile().mkdirs();
    }
    OutputStream os = null;
    try {
      os = new FileOutputStream(to);
      byte[] buffer = new byte[65536];
      int count = from.read(buffer);
      while (count > 0) {
        os.write(buffer, 0, count);
        count = from.read(buffer);
      } 
    } finally {
      if (os != null)
        os.close(); 
    } 
  }
}
