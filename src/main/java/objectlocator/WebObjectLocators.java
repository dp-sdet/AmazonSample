package objectlocator;

import exception.CustomException;
import objectlocator.ObjectLocators;
import core.EnvParameters;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;



public class WebObjectLocators
  extends ObjectLocators
{
  static  {
	  FileInputStream webStream = null;
    try {
     
        webStream = new FileInputStream(EnvParameters.TEST_ROOT_DIR + File.separator + "ObjectRepo" + File.separator + "PROP" + File.separator + "Web_ObjectRepository.properties");
        
        props.load(webStream);
      
    
    }
    catch (IOException e) {
      props = null;
    } 
  }











  
  public static String getLocator(String locatorName) throws CustomException {
    if (props == null)
    {
      
      throw new CustomException("Failed to read: Web_ObjectRepository.properties -> It is either not present or not readable");
    }

    
    return props.getProperty(locatorName);
  }


}
