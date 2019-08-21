package Utils;

import exception.ItJumpStartReportException;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.Reporter;













public class Utils
{
  public static final int DEFAULT_FILE_SIZE_KB = 1;
  public static final String IMG_FORMAT_PNG = ".png";
  public static final String IMG_FORMAT_JPG = ".jpg";
  public static final String RUN_MODE = "maven";
  public static final String DT_FORMAT_MMDDYYYY = "MM/dd/yyyy";
  public static final String DT_FORMAT_DDMMYYYY = "dd/MM/yyyy";
  
  public static void createFolder(String strDirectoy) {
    boolean success = (new File(strDirectoy)).mkdirs();
    if (!success && !(new File(strDirectoy)).exists()) {
      throw new ItJumpStartReportException("Failed to create directory" + strDirectoy);
    }
  }
  
  public static String getCurrentDateTime(String strFormat) {
    Date currentDate = new Date();
    SimpleDateFormat newFormat = new SimpleDateFormat(strFormat);
    return newFormat.format(currentDate);
  }





  
  public static void takeScreenShot(String ImageFileName) {
    Robot robot = null;
    try {
      robot = new Robot();
    } catch (AWTException e1) {
      e1.printStackTrace();
    } 
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Dimension screenSize = toolkit.getScreenSize();
    Rectangle screenRect = new Rectangle(screenSize);
    
    BufferedImage image = robot.createScreenCapture(screenRect);
    try {
      File file;
      int count = 0;
      String currentImageFilePath = null;


      
      do {
        currentImageFilePath = ImageFileName + ((count == 0) ? "" : Integer.valueOf(count)) + ".png";
        file = new File(currentImageFilePath);
        count++;
      } while (file.exists());
      ImageIO.write(image, "png", new File(currentImageFilePath));
      
      Reporter.log("Final Screenshot:<br><a href='file:///" + currentImageFilePath + "' target='new'> <img src='file:///" + currentImageFilePath + "' width='300px' height='200px' /></a><br> ");

    
    }
    catch (IOException e) {
      e.printStackTrace();
    } 
  }




















  
  public static String randomFileName() { return RandomStringUtils.randomAlphanumeric(1 + (new Random()).nextInt(25)); }






  
  public static void copyFile(String srcFilePath, String destFilePath) {
    File srcFile = new File(srcFilePath);
    File destFile = new File(destFilePath);
    try {
      FileUtils.copyFile(srcFile, destFile);
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }







  
  public static void captureScreen(String folderPath, String fileName) {
    if (!(new File(folderPath)).isDirectory()) {
      try {
        FileUtils.forceMkdir(new File(folderPath));
      } catch (IOException e1) {
        e1.printStackTrace();
      } 
    }
    String filePath = folderPath + File.separator + fileName + ".jpg";
    
    try {
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Rectangle screenRectangle = new Rectangle(screenSize);
      Robot robot = new Robot();
      BufferedImage image = robot.createScreenCapture(screenRectangle);
      ImageIO.write(image, "jpg", new File(filePath));
    } catch (Exception e) {
      System.out.println("Exception thrown while capturing screenshot '" + filePath + "' : " + e
          .toString());
    } 
  }



  
  public static void deleteDirectory(String folderPath) {
    try {
      FileUtils.deleteDirectory(new File(folderPath));
    } catch (IOException e) {
      System.out.println("Exeception deleting folder : " + folderPath);
      e.printStackTrace();
    } 
  }




  
  public static String getCurrentDate(String format) {
    DateFormat dateFormat;
    if (format.equals("dd/MM/yyyy")) {
      dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    } else if (format.equals("MM/dd/yyyy")) {
      dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    } else {
      dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    } 
    Date date = new Date();
    return dateFormat.format(date);
  }







  
  public static String getRelativePath(String absPath, String basePath) {
    try {
      Path pathAbsolute = Paths.get(absPath, new String[0]);
      Path pathBase = Paths.get(basePath, new String[0]);
      Path pathRelative = pathBase.relativize(pathAbsolute);
      return pathRelative.toString();
    } catch (Exception e) {
      return absPath;
    } 
  }
  
  public String getVersion() {
    String version = null;

    
    try {
      Properties p = new Properties();
      InputStream is = getClass().getResourceAsStream("/META-INF/maven/com.pearson.autobahn/schema-registry/pom.properties");
      
      if (is != null) {
        p.load(is);
        version = p.getProperty("version", "");
      } 
    } catch (Exception e) {}



    
    if (version == null) {
      Package aPackage = getClass().getPackage();
      if (aPackage != null) {
        version = aPackage.getImplementationVersion();
        if (version == null) {
          version = aPackage.getSpecificationVersion();
        }
      } 
    } 
    
    if (version == null)
    {
      version = "";
    }
    
    return version;
  }
}
