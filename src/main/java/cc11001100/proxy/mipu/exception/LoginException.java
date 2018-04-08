package cc11001100.proxy.mipu.exception;

/**
 * @author CC11001100
 */
public class LoginException extends RuntimeException {

	public LoginException() {
		super();
	}

	public LoginException(String message) {
		super(message);
	}

	public LoginException(String message, Throwable cause) {
		super(message, cause);
	}

	public LoginException(Throwable cause) {
		super(cause);
	}

	protected LoginException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
