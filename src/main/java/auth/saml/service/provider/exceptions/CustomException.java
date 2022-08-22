package auth.saml.service.provider.exceptions;

/**
 * A base or generic exception for problems specific to customization api
 * related errors
 * 
 * @author Deoyani Nandrekar-Heinis
 *
 */

public class CustomException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3549633360117422044L;

	/**
	 * Create an exception with an arbitrary message
	 */
	public CustomException(String msg) {
		super(msg);
	}

	/**
	 * Create an exception with an arbitrary message and an underlying cause
	 */
	public CustomException(String msg, Throwable cause) {
		super(msg, cause);
	}

	/**
	 * Create an exception with an underlying cause. A default message is created.
	 */
	public CustomException(Throwable cause) {
		super(messageFor(cause), cause);
	}

	/**
	 * return a message prefix that can introduce a more specific message
	 */
	public static String getMessagePrefix() {
		return "Customization API exception encountered: ";
	}

	protected static String messageFor(Throwable cause) {
		StringBuilder sb = new StringBuilder(getMessagePrefix());
		String name = cause.getClass().getSimpleName();
		if (name != null)
			sb.append('(').append(name).append(") ");
		sb.append(cause.getMessage());
		return sb.toString();
	}

}
