package exception;

/**
 * To handle Reporting exceptions that might occur during test run.
 */

import java.io.Serializable;

public class ReportConfigException extends RuntimeException implements Serializable {
	private static final long serialVersionUID = -3644742957748395150L;

	public ReportConfigException() {
	}

	public ReportConfigException(String msg) {
		super(msg);
	}

	public ReportConfigException(String message, Throwable cause) {
		super(message, cause);
	}
}
