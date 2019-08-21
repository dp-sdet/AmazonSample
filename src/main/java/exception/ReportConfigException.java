package exception;

import java.io.Serializable;







public class ReportConfigException
  extends RuntimeException
  implements Serializable
{
  private static final long serialVersionUID = -3644742957748395150L;
  
  public ReportConfigException() {}
  
  public ReportConfigException(String msg) { super(msg); }








  
  public ReportConfigException(String message, Throwable cause) { super(message, cause); }
}
