package cc11001100.proxy.mipu.exception;

/**
 * @author CC11001100
 */
public class RegisterException extends RuntimeException {
	public RegisterException() {
		super();
	}

	public RegisterException(String message) {
		super(message);
	}

	public RegisterException(String message, Throwable cause) {
		super(message, cause);
	}

	public RegisterException(Throwable cause) {
		super(cause);
	}

	protected RegisterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
