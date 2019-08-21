package report;

import exception.ItJumpStartReportException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;








public class PropLoader
{
  public Properties loadProperties(String fileName) {
    Properties prop = new Properties();
    InputStream is = null;
    try {
      is = new FileInputStream(fileName);
      prop.load(is);
    } catch (IOException e) {
      throw new ItJumpStartReportException("Unable to load Property File " + fileName, e);
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (IOException e) {
          throw new ItJumpStartReportException("Unable to close Property File " + fileName, e);
        } 
      }
    } 
    return prop;
  }
}
