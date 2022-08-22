package auth.saml.service.provider.service;

/**
 * Exception thrown at runtime when requested resource is not available.
 * 
 * @author Deoyani Nandrekar-Heinis
 *
 */
public class ResourceNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2006356489223592443L;
	private String requestUrl = "";

	/**
	 * ResourceNotFoundException for given Id
	 * 
	 * @param id
	 */
	public ResourceNotFoundException(int id) {
		super("ResourceNotFoundException with id=" + id);
	}

	/**
	 * ResourceNotFoundException
	 */
	public ResourceNotFoundException() {
		super("Resource you are looking for is not available.");
	}

	/***
	 * ResourceNotFoundException for requestUrl
	 * 
	 * @param requestUrl String
	 */
	public ResourceNotFoundException(String requestUrl) {

		super("Resource you are looking for is not available.");
		this.setRequestUrl(requestUrl);
	}

	/***
	 * GetRequestURL
	 * 
	 * @return String
	 */
	public String getRequestUrl() {
		return this.requestUrl;
	}

	/***
	 * Set Request URL
	 * 
	 * @param url String
	 */
	public void setRequestUrl(String url) {
		this.requestUrl = url;
	}
}
