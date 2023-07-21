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
	
//	@Value("${saml.attribute.claim.usergroup}")
//	private String usergroup;
//
//	@Value("${saml.attribute.claim.nistdivisionname}")
//	private String divisionname;
//
//	@Value("${saml.attribute.claim.nistdivisionnumber}")
//	private String divisionnumber;
//
	@Value("${saml.attribute.claim.nistouacr}")
	private String ouacr;

	@Override
	public Object loadUserBySAML(SAMLCredential credential) throws UsernameNotFoundException {
		String userEmail1 = credential.getAttributeAsString(email);
		System.out.println("userEmail1:" + userEmail1);
//		String group = credential.getAttributeAsString(usergroup);
//		String division = credential.getAttributeAsString(divisionname);
//		String divnumber = credential.getAttributeAsString(divisionnumber);
		String OUacr = credential.getAttributeAsString(ouacr);
		AuthenticatedUserDetails samUser = new AuthenticatedUserDetails(credential.getAttributeAsString(email),
				credential.getAttributeAsString(name), credential.getAttributeAsString(lastname),
				credential.getAttributeAsString(userid), OUacr
				);
		return samUser;
	}
}