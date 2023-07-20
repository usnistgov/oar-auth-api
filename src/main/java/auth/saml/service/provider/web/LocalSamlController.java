package auth.saml.service.provider.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import auth.saml.service.provider.helpers.AuthenticatedUserDetails;


/**
 * This controller is added for testing the api locally without having to
 * connect to external identity provider.
 * 
 * @author Deoyani S Nandrekar-Heinis
 *
 */
@RestController
@Validated
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/saml/login")
//@Profile({ "local" })  //This setting can be used to enable the feature based on certain profiles/platforms.
@ConditionalOnProperty(value = "samlauth.enabled", havingValue = "false", matchIfMissing = false)
public class LocalSamlController {
	private Logger logger = LoggerFactory.getLogger(LocalSamlController.class);

	@RequestMapping(method = RequestMethod.GET)
	public RedirectView redirect(@RequestParam String redirectTo, HttpServletRequest req) {
		System.out.print("test:" + redirectTo);
		logger.info("This should be called only while running locally. This authenticates all the requests.");
		AuthenticatedUserDetails authDetails = new AuthenticatedUserDetails("testuser@test.com", "TestUser",
				"TestLast", "TestId");
		Authentication auth = new UsernamePasswordAuthenticationToken(authDetails, "guestpass");
		// auth.setAuthenticated(true);
		SecurityContext sc = SecurityContextHolder.getContext();
		sc.setAuthentication(auth);
		HttpSession session = req.getSession(true);
//		session.setMaxInactiveInterval(3600);
		session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);
		return new RedirectView(redirectTo);
	}

}
