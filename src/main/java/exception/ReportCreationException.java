package exception;

/**
 * To handle report creation exceptions that might occur during test run.
 */

import java.io.Serializable;

public class ReportCreationException extends RuntimeException implements Serializable {
	private static final long serialVersionUID = -1546026899179040910L;

	public ReportCreationException() {
	}

	public ReportCreationException(String msg) {
		super(msg);
	}

	public ReportCreationException(String message, Throwable cause) {
		super(message, cause);
	}
}
