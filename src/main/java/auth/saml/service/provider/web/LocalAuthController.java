package auth.saml.service.provider.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import auth.saml.service.provider.exceptions.BadGetwayException;
import auth.saml.service.provider.exceptions.CustomException;
import auth.saml.service.provider.exceptions.ErrorInfo;
import auth.saml.service.provider.exceptions.UnAuthenticatedUserException;
import auth.saml.service.provider.exceptions.UnAuthorizedUserException;
import auth.saml.service.provider.helpers.AuthenticatedUserDetails;
import auth.saml.service.provider.service.JWTTokenGenerator;
import auth.saml.service.provider.service.UserToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;



/**
 * This controller is added for testing the api locally without having to
 * connect to authorization service.
 * 
 * @author Deoyani S Nandrekar-Heinis
 *
 */
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/auth")
//@Profile({ "local" }) //This setting can be used to enable the feature based on certain profiles/platforms.
@ConditionalOnProperty(value = "samlauth.enabled", havingValue = "false", matchIfMissing = false)
public class LocalAuthController {
	private Logger logger = LoggerFactory.getLogger(LocalAuthController.class);

	@Autowired
	JWTTokenGenerator jwt;

	
	/**
	 * Get the JWT for the authorized user
	 * 
	 * @param authentication
	 * @param ediid
	 * @return JSON with userid and token
	 * @throws UnAuthorizedUserException
	 * @throws CustomException
	 * @throws UnAuthenticatedUserException
	 */
	@RequestMapping(value = { "/_tokeninfo" }, method = RequestMethod.GET, produces = "application/json")
	@Parameters ({
	    @Parameter(name = "Authentication", description = "authentication object.")})
	   	@Operation(summary = "Get the authorized token.", description = "Resource returns a JSON if Authorized user.")
	public UserToken token( Authentication authentication )
			throws UnAuthorizedUserException, CustomException, UnAuthenticatedUserException, BadGetwayException {

		try {

			if (authentication == null || authentication.getPrincipal().equals("anonymousUser"))
				throw new UnAuthenticatedUserException(" User is not authenticated to access this resource.");
			logger.info("Get the token for authenticated user.");
			AuthenticatedUserDetails pauth = (AuthenticatedUserDetails) authentication.getPrincipal();
			return jwt.getJWT(pauth);
		} catch (UnAuthorizedUserException ex) {

				throw ex;
		}

	}
	
	/**
	 * Get Authenticated user information
	 * 
	 * @param response
	 * @return JSON user id
	 * @throws IOException
	 */

	@RequestMapping(value = { "/_logininfo" }, method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<AuthenticatedUserDetails> loginLocal(HttpServletResponse response, Authentication authentication) throws IOException {
		logger.info("Get the authenticated user info.");
		if (authentication == null) {
			response.sendRedirect("/saml/login");
		} else {
			AuthenticatedUserDetails pauth = (AuthenticatedUserDetails) authentication.getPrincipal();
			
			return new ResponseEntity<>(pauth, HttpStatus.OK);
		}
		return null;
	}
	/**
	 * Exception handling if user is not authorized
	 * 
	 * @param ex
	 * @param req
	 * @return
	 */
	@ExceptionHandler(UnAuthorizedUserException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ErrorInfo handleStreamingError(UnAuthorizedUserException ex, HttpServletRequest req) {
		logger.info("There user requesting edit access is not authorized : " + req.getRequestURI() + "\n  "
				+ ex.getMessage());
		return new ErrorInfo(req.getRequestURI(), 401, "UnauthroizedUser", req.getMethod());
	}

	/**
	 * Exception handling if user is not authorized
	 * 
	 * @param ex
	 * @param req
	 * @return
	 */
	@ExceptionHandler(UnAuthenticatedUserException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ErrorInfo handleStreamingError(UnAuthenticatedUserException ex, HttpServletRequest req) {
		logger.info("There user requesting edit access is not authorized : " + req.getRequestURI() + "\n  "
				+ ex.getMessage());
		return new ErrorInfo(req.getRequestURI(), 401, "UnAuthenticated", req.getMethod());
	}
}
