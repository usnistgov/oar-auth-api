package auth.saml.service.provider.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;

import auth.saml.service.provider.helpers.AuthenticatedUserDetails;



/**
 * This service is called by SAML authentication provider.
 * 
 * @author Deoyani Nandrekar-Heinis
 */
public class SamlUserDetailsService implements SAMLUserDetailsService {

	@Value("${saml.attribute.claim.email}")
	private String email;

	@Value("${saml.attribute.claim.lastname}")
	private String lastname;

	@Value("${saml.attribute.claim.name}")
	private String name;

	@Value("${saml.attribute.claim.userid}")
	private String userid;

	@Override
	public Object loadUserBySAML(SAMLCredential credential) throws UsernameNotFoundException {
		String userEmail1 = credential.getAttributeAsString(email);
		System.out.println("userEmail1:" + userEmail1);
		AuthenticatedUserDetails samUser = new AuthenticatedUserDetails(credential.getAttributeAsString(email),
				credential.getAttributeAsString(name), credential.getAttributeAsString(lastname),
				credential.getAttributeAsString(userid));
		return samUser;
	}
}