package auth.saml.service.provider.helpers;

import java.io.Serializable;

/**
 * AuthenticatedUserDetails class presents details of the user authenticated by
 * syste, In this case, it represents short system userId, User's name, User's
 * last name, User's emailid
 * 
 * @author Deoyani Nandrekar-Heinis
 *
 */
public class AuthenticatedUserDetails implements Serializable {

	/**
	 * Serial version generated for this serializable class
	 */
	private static final long serialVersionUID = 2968533695286307068L;
	/**
	 * Short system user id
	 */
	private String userId;
	/**
	 * User's First Name
	 */
	private String userName;
	/**
	 * User's Last Name
	 */
	private String userLastName;
	/**
	 * User's email id
	 */
	private String userEmail;

	
	/**
	 * Short system user group
	 */
	private String userGroup;
	/**
	 * User's  Division
	 */
	private String userDiv;
	/**
	 * User's  Division number
	 */
	private String userDivNum;
	/**
	 * User's OU
	 */
	private String userOU;
	
	public AuthenticatedUserDetails() {
	}

	public AuthenticatedUserDetails(String userEmail, String userName, String userLastName, String userId, 
			String userGroup, String userDiv, String userDivNum, String userOU) {
		this.userId = userId;
		this.userName = userName;
		this.userLastName = userLastName;
		this.userEmail = userEmail;
		this.userGroup = userGroup;
		this.userDiv = userDiv;
		this.userDivNum = userDivNum;
		this.userOU = userOU;
	}

	/**
	 * Set the User Id
	 * 
	 * @param userId
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * Set the user's first name
	 * 
	 * @param userName
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Set User's Last Name
	 * 
	 * @param userLastName
	 */
	public void setUserLastName(String userLastName) {
		this.userLastName = userLastName;
	}

	
	
	
	/**
	 * Set User's email
	 * 
	 * @param userEmail
	 */
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	
	
	/**
	 * Set User's Division
	 * 
	 * @param userDiv
	 */
	public void setUserDiv(String userDiv) {
		this.userDiv = userDiv;
	}

	
	/**
	 * Set User's Division Number
	 * 
	 * @param userDivNum
	 */
	public void setUserDivNum(String userDivNum) {
		this.userDivNum = userDivNum;
	}
	
	
	/**
	 * Set User's group
	 * 
	 * @param userGroup
	 */
	public void setUserGroup(String userGroup) {
		this.userGroup = userGroup;
	}
	
	
	/**
	 * Set User's OU
	 * 
	 * @param userOU
	 */
	public void setUserOU(String userOU) {
		this.userOU = userOU;
	}
	/**
	 * Get User's short Id
	 * 
	 * @return
	 */
	public String getUserId() {
		return this.userId;
	}

	/**
	 * Get User's first name
	 * 
	 * @return
	 */
	public String getUserName() {
		return this.userName;
	}

	/**
	 * Get User's last name
	 * 
	 * @return
	 */
	public String getUserLastName() {
		return this.userLastName;
	}

	/**
	 * Get User's email
	 * 
	 * @return
	 */
	public String getUserEmail() {
		return this.userEmail;
	}

	/**
	 * Get User's Division
	 * 
	 * @param userDiv
	 */
	public String getUserDiv() {
		return this.userDiv;
	}

	
	/**
	 * Get User's Division Number
	 * 
	 * @param userDivNum
	 */
	public String getUserDivNum() {
		return this.userDivNum;
	}
	
	
	/**
	 * Get User's group
	 * 
	 * @param userGroup
	 */
	public String getUserGroup() {
		return this.userGroup;
	}
	
	
	/**
	 * Get User's OU
	 * 
	 * @param userOU
	 */
	public String getUserOU() {
		return this.userOU;
	}
}
