package exception;
/**
 * To handle browser timeout exception
 */

public class BrowserBotException extends RuntimeException {

	private static final long serialVersionUID = 9102369783706142278L;

	public BrowserBotException() {
		super();
	}

	public BrowserBotException(String msg) {
		super(msg);
	}

	/**
	 * For wrapping up exception
	 * 
	 * @param message
	 * @param cause
	 */
	public BrowserBotException(String message, Throwable cause) {
		super(message, cause);
	}

}
