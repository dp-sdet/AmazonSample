package objectlocator;

import exception.CustomException;
import java.io.File;
import java.util.Properties;
import org.jdom2.input.SAXBuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;








public class ObjectLocators
{
  protected WebDriver driver;
  protected WebDriverWait wait;
  protected static Properties props = new Properties();


  
  protected static SAXBuilder builder;


  
  protected static File xmlFile;


  
  public static By getBySelector(String propKey) {
    String[] split = propKey.split(";");
    String type = split[0];

    
    if (type.equalsIgnoreCase("id"))
      return By.id(split[1]); 
    if (type.equalsIgnoreCase("css"))
      return By.cssSelector(split[1]); 
    if (type.equalsIgnoreCase("tagname"))
      return By.tagName(split[1]); 
    if (type.equalsIgnoreCase("classname") || type.equalsIgnoreCase("class"))
      return By.className(split[1]); 
    if (type.equalsIgnoreCase("name"))
      return By.name(split[1]); 
    if (type.equalsIgnoreCase("xpath"))
      return By.xpath(split[1]); 
    if (type.equalsIgnoreCase("link"))
      return By.linkText(split[1]); 
  
    
    throw new CustomException("Invalid element locator parameter -" + propKey);
  }
}
