package Utils;

import exception.CustomException;
import report.LoggerUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.testng.Reporter;













public abstract class OSUtils
{
  public enum OSType
  {
    windows, windows64;
  }


  
  public static String twoStringsWithNewLine(String one, String two) {
    String separator = System.getProperty("line.separator");
    StringBuilder lines = new StringBuilder(one);
    lines.append(separator);
    lines.append(two);
    return lines.toString();
  }







  
  public static boolean isProcessRuning(String serviceName) {
    if (getOSname() == OSType.windows) {
      
      try {
        Process p = Runtime.getRuntime().exec("tasklist");
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        
        String line;
        while ((line = reader.readLine()) != null) {
          if (line.contains(serviceName)) {
            Reporter.log(serviceName + " is running", true);
            return true;
          } 
        } 
      } catch (IOException e) {
        e.printStackTrace();
      } 
      return false;
    } 
    LoggerUtil.log("Client OS is not Windows, cannot check running process: " + serviceName);

    
    return false;
  }






  
  public static void killProcess(String serviceName) {
    Reporter.log("Trying to kill " + serviceName, true);
    
    String KILL = "taskkill /F /T /IM ";
    if (getOSname() == OSType.windows) {
      try {
        Runtime.getRuntime().exec(KILL + serviceName);
      } catch (IOException e) {
        e.printStackTrace();
      } 
    } else {
      Reporter.log("Client OS is not window, can not kill " + serviceName, true);
    } 
  }






 
  public static void createFolder(String strDirectoy) {
    boolean success = (new File(strDirectoy)).mkdirs();
    if (success);
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
  
  public static File lastFileModified(String dir) {
    File fl = new File(dir);
    File[] files = fl.listFiles(new FileFilter() {
          public boolean accept(File file) {
            return file.isFile();
          }
        });
    long lastMod = (long) Float.MIN_VALUE;
    File choice = null;
    for (File file : files) {
      if (file.lastModified() > lastMod) {
        choice = file;
        lastMod = file.lastModified();
      } 
    } 
    return choice;
  }

  
  public static boolean renameFile(File oldFile, File newFile) {
    if (oldFile.renameTo(newFile)) {
      return true;
    }
    return false;
  }



  
  public static String generateUniqueId() { return Long.toString(System.currentTimeMillis()); }







  
  public static OSType getOSname() {
    String osType = System.getProperty("os.name");
    
    return getOSname(osType);
  }







  
  public static OSType getOSname(String osType) {
    if (osType.toLowerCase().contains("win")) {
      if (System.getenv("PROCESSOR_ARCHITECTURE").contains("86") && 
        System.getenv("PROCESSOR_ARCHITEW6432") != null) {
        return OSType.windows;
      }
      return OSType.windows;
    } 
    if (osType.toLowerCase().contains("win") && System.getProperty("os.arch").equalsIgnoreCase("x64"))
      return OSType.windows64; 
    
    return OSType.windows;
  }

}
