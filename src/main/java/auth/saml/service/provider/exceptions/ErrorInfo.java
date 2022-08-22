package auth.saml.service.provider.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * a simple container for communicating data about a web service error to the
 * web client. An instance can be automatically converted to a JSON-formatted
 * response by the Spring web framework.
 * <p>
 * Note that, generally, web services should not reflect back inputs from the
 * client back to the client without some scrubbing of that input; this can be a
 * vector for web site injection attacks.
 * <p>
 * This container leverages the Jackson JSON framework (which is used by the
 * Spring Framework) for serializing this information into JSON.
 */
@JsonInclude(Include.NON_NULL)
public class ErrorInfo {

	/**
	 * the (encoded) URL path.
	 */
	public String requestURL = null;

	/**
	 * the HTTP method used
	 */
	public String method = null;

	/**
	 * the HTTP error status returned
	 */
	public int status = 0;

	/**
	 * an error message or explanation
	 */
	public String message = null;

	/**
	 * create the response
	 */
	public ErrorInfo(int httpstatus, String reason) {
		status = httpstatus;
		message = reason;
	}

	/**
	 * create the response. GET is assumed as the method used
	 */
	public ErrorInfo(String url, int httpstatus, String reason) {
		this(url, httpstatus, reason, "GET");
	}

	/**
	 * create the response
	 * 
	 * @param url        the encoded URL accessed by the client. The output of
	 *                   HttpServletRequest.getRequestURI() is the recommended value
	 *                   as this string will generally be encoded.
	 * @param httpstatus the HTTP status code accompanying this error response
	 * @param reason     an explanatory error message. (Note: details are not
	 *                   recommended for status &gt; 500.)
	 * @param httpmeth   the HTTP method used by the client (e.g. "GET", "HEAD",
	 *                   etc.)
	 */
	public ErrorInfo(String url, int httpstatus, String reason, String httpmeth) {
		status = httpstatus;
		message = reason;
		requestURL = url;
		method = httpmeth;
	}
}
