package exception;

/**
 * To handle runtime and custom exceptions that might occur during test run.
 */



public class CustomException
  extends RuntimeException
{
  private static final long serialVersionUID = 9102369783706142278L;
  
  public CustomException() {}
  
  public CustomException(String msg) { super(msg); }

  public CustomException(String message, Throwable cause) { super(message, cause); }
}
