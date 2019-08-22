package exception;

/**
 * To handle JumpStartReportException exceptions that might occur during test run.
 */
import java.io.Serializable;

public class ItJumpStartReportException extends RuntimeException implements Serializable {
	private static final long serialVersionUID = 6451047615995283896L;

	public ItJumpStartReportException() {
	}

	public ItJumpStartReportException(String msg) {
		super(msg);
	}

	public ItJumpStartReportException(String message, Throwable cause) {
		super(message, cause);
	}
}
