package auth.saml.service.provider.service;

import java.io.Serializable;

import auth.saml.service.provider.helpers.AuthenticatedUserDetails;



/**
 * This class is to store user id and JWT information for authorized user.
 * It also has field to represent error message in case user is authenticated 
 * but authorization process generates some error and no token is generated.
 * 
 * @author Deoyani Nandrekar-Heinis
 *
 */
public class UserToken implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3414986086109823716L;
	private String token;
	private String errorMessage;

	public UserToken( String token, String errorMessage) {
		this.token = token;
		this.errorMessage = errorMessage;
	}
	
	public UserToken( String token) {
		this.token = token;
		
	}

	/**
	 * get the JWT generated for authorized user
	 * @return String
	 */
	public String getToken() {
		return token;
	}

	/**
	 * Set the JWT generated for authorized user
	 * @param token
	 */
	public void setToken(String token) {
		this.token = token;
	}

	
	/***
	 * Set this error message if there is an error while token is generated 
	 * for example due to back end authorization service response.
	 * @param errorMessage
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	/***
	 * Get any error message associated with the user while authorizing user and no token is generated.
	 * @return
	 */
	public String getErrorMessage() {
		return this.errorMessage;
	}
}