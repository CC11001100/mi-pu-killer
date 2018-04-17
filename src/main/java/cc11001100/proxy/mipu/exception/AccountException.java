package cc11001100.proxy.mipu.exception;

/**
 * @author CC11001100
 */
public class AccountException extends RuntimeException {

	public AccountException() {
		super();
	}

	public AccountException(String message) {
		super(message);
	}

	public AccountException(String message, Throwable cause) {
		super(message, cause);
	}

	public AccountException(Throwable cause) {
		super(cause);
	}

	protected AccountException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
