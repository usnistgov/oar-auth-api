package auth.saml.service.provider.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsExtractor {

	private static final Logger logger = LoggerFactory.getLogger(UserDetailsExtractor.class);

	@Value("${saml.attribute.claim.email}")
	private String emailAttribute;

	@Value("${saml.attribute.claim.lastname}")
	private String lastnameAttribute;

	@Value("${saml.attribute.claim.name}")
	private String nameAttribute;

	@Value("${saml.attribute.claim.userid}")
	private String useridAttribute;
	
	@Value("${saml.attribute.claim.usergroup}")
	private String usergroup;

	@Value("${saml.attribute.claim.nistdivisionname}")
	private String divisionname;

	@Value("${saml.attribute.claim.nistdivisionnumber}")
	private String divisionnumber;

	@Value("${saml.attribute.claim.nistouname}")
	private String ouname;

	/**
	 * Return userId if authenticated user and in context else return empty string
	 * if no user can be extracted.
	 * 
	 * @return String userId
	 */

	public AuthenticatedUserDetails getUserDetails() {
		AuthenticatedUserDetails authUser = new AuthenticatedUserDetails();
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			SAMLCredential credential = (SAMLCredential) auth.getCredentials();
			String lastName = credential.getAttributeAsString(lastnameAttribute);
			String name = credential.getAttributeAsString(nameAttribute);
			String email = credential.getAttributeAsString(emailAttribute);
			String userid = credential.getAttributeAsString(useridAttribute);
			String group = credential.getAttributeAsString(usergroup);
			String division = credential.getAttributeAsString(divisionname);
			String divnumber = credential.getAttributeAsString(divisionnumber);
			String OU = credential.getAttributeAsString(ouname);
			authUser = new AuthenticatedUserDetails(email, name, lastName, userid, group,division,divnumber,OU);

		} catch (Exception exp) {
			logger.error("No user is authenticated and return empty userid");
		}
		return authUser;
	}

	/**
	 * Parse requestURL and get the record id which is a path parameter
	 * 
	 * @param requestURI
	 * @return String recordid
	 */
	public String getUserRecord(String requestURI) {
		String recordId = "";
		try {
			recordId = requestURI.split("/editor/")[1];
		} catch (ArrayIndexOutOfBoundsException exp) {

			logger.error("No record id is extracted from request URL so empty string is returned");
			recordId = "";

		}
		return recordId;
	}

}
