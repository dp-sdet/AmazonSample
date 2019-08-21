package report;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;













public class ResultContainer
{
  private WebDriver driver;
  private File screenshot;
  private BufferedImage desktopScreenshot;
  private String screeshotfile;
  private String desktopScreeshotfile;
  private String webpage;
  private String webpagefile;
  private String videofile;
  
  public ResultContainer(WebDriver driver) { this.driver = driver; }



  
  public ResultContainer() {}


  
  public void setScreenshot() {
    File scrFile = null;
    try {
      scrFile = (File)((TakesScreenshot)this.driver).getScreenshotAs(OutputType.FILE);
    } catch (ClassCastException cce) {
      LoggerUtil.log("capturing Augmented screenshot: -> " + cce.getMessage(), Level.DEBUG);
      scrFile = (File)((TakesScreenshot)(new Augmenter()).augment(this.driver)).getScreenshotAs(OutputType.FILE);
    } catch (Exception e) {

      
      LoggerUtil.log("Unable to capture the screenshot:" + e.getMessage(), Level.WARN);
    } 
    this.screenshot = scrFile;
  }
  
  public void takeDesktopScreenshot() {
    Robot robot = null;
    try {
      robot = new Robot();
      
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      Dimension screenSize = toolkit.getScreenSize();
      Rectangle screenRect = new Rectangle(screenSize);
      
      this.desktopScreenshot = robot.createScreenCapture(screenRect);
    }
    catch (AWTException e1) {
      LoggerUtil.log("Unable to capture Desktop screenshot:" + e1.getMessage(), Level.DEBUG);
    } catch (Exception e) {
      LoggerUtil.log("Unable to capture Desktop screenshot:" + e.getMessage(), Level.DEBUG);
    } 
  }

  
  public File getScreenshot() { return this.screenshot; }


  
  public String getWebpage() { return this.webpage; }

  
  public void setWebpage() {
    try {
      this.webpage = this.driver.getPageSource();
    } catch (Exception e) {
      LoggerUtil.log("Unable to capture the webpage:" + e.getMessage(), Level.WARN);
    } 
  }

  
  public String getVideofile() { return this.videofile; }


  
  public void setVideofile(String videofile) { this.videofile = videofile; }



  
  public void saveScreenshot(String targetLocation) {
    try {
      FileUtils.copyFile(this.screenshot, new File(targetLocation.replaceAll("\\*", "")));
      this.screeshotfile = targetLocation;
    } catch (Exception e) {
      LoggerUtil.log(e.getMessage(), Level.DEBUG);
      this.screeshotfile = null;
    } 
  }


  
  public void saveDesktopScreenshot(String targetLocation) {
    try {
      ImageIO.write(this.desktopScreenshot, "png", new File(targetLocation));
      
      this.desktopScreeshotfile = targetLocation;
    } catch (Exception e) {
      LoggerUtil.log(e.getMessage(), Level.DEBUG);
      this.screeshotfile = null;
    } 
  }

  
  public void saveWebPage(String targetLocation) {
    BufferedWriter writer = null;
    try {
      File scrFile = new File(targetLocation);
      try {
        writer = new BufferedWriter(new FileWriter(scrFile));
        writer.write(this.webpage);
        writer.close();
        this.webpagefile = targetLocation;
      } catch (IOException e1) {
        LoggerUtil.log(e1.getMessage(), Level.WARN);
        this.webpagefile = null;
      } 
    } catch (Exception e) {
      LoggerUtil.log(e.getMessage(), Level.WARN);
      this.webpagefile = null;
    } 
  }




  
  public String getScreeshotfile() { return this.screeshotfile; }





  
  public String getWebpagefile() { return this.webpagefile; }





  
  public String getDesktopScreeshotfile() { return this.desktopScreeshotfile; }
}
