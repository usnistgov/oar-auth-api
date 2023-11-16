
package auth.saml.service.provider.config.SAMLConfig;


import java.net.MalformedURLException;
import java.net.URL;

import org.opensaml.ws.transport.http.HTTPOutTransport;
import org.opensaml.ws.transport.http.HttpServletRequestAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.saml.SAMLEntryPoint;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.security.saml.websso.WebSSOProfileOptions;



/***
 * This helps SAML endpoint to redirect after successful login.
 * 
 * @author Deoyani Heinis
 *
 */
public class SamlWithRelayStateEntryPoint extends SAMLEntryPoint {
	private static final Logger log = LoggerFactory.getLogger(SamlWithRelayStateEntryPoint.class);

	private String defaultRedirect;

	public SamlWithRelayStateEntryPoint(String applicationURL) {
		this.defaultRedirect = applicationURL;
	}

	
	@Override
	protected WebSSOProfileOptions getProfileOptions(SAMLMessageContext context, AuthenticationException exception) {

		String[] defaultRedirectURLs = defaultRedirect.split(",");
		
		WebSSOProfileOptions ssoProfileOptions;
		if (defaultOptions != null) {
			ssoProfileOptions = defaultOptions.clone();
			
		} else {
			ssoProfileOptions = new WebSSOProfileOptions();
		}
		System.out.println("ssoProfileOptions :"+ssoProfileOptions.getRelayState());

		// Note for customization :
		// Original HttpRequest can be extracted from the context param
		// caller can pass redirect url with the request so after successful processing
		// user can be redirected to the same page.
		// if redirect URL is not specified user will be redirected to default url.

		HttpServletRequestAdapter httpServletRequestAdapter = (HttpServletRequestAdapter) context
				.getInboundMessageTransport();

		String redirectURL = httpServletRequestAdapter.getParameterValue("redirectTo");
        boolean allowed = false;
		for (String urlString : defaultRedirectURLs) {
			if (redirectURL.startsWith(urlString))
				allowed = true; break;
		}
		
		if (redirectURL != null && allowed) {
			log.info("Redirect user to +" + redirectURL);		
			ssoProfileOptions.setRelayState(redirectURL);
		} else {
			log.info("Redirect user to default URL");
			ssoProfileOptions.setRelayState(defaultRedirectURLs[0]);
		}

		return ssoProfileOptions;
	}

}