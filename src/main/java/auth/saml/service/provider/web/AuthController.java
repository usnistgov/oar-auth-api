package auth.saml.service.provider.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
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
import auth.saml.service.provider.helpers.UserDetailsExtractor;
import auth.saml.service.provider.service.JWTTokenGenerator;
import auth.saml.service.provider.service.ResourceNotFoundException;
import auth.saml.service.provider.service.UserToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;


/**
 * This controller sends JWT, a token generated after successful authentication.
 * This token can be used to further communication with service. 
 * 
 * @author Deoyani Nandrekar-Heinis
 */
@RestController
@RequestMapping("/auth")
//@Profile({"prod","dev","test","default"})
@ConditionalOnProperty(value = "samlauth.enabled", havingValue = "true", matchIfMissing = true)
public class AuthController {

	private Logger logger = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	JWTTokenGenerator jwt;

	@Autowired
	UserDetailsExtractor uExtract;

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
	@RequestMapping(value = { "_perm/{ediid}" }, method = RequestMethod.GET, produces = "application/json")
	@Parameters ({
	    @Parameter(name = "Authentication", description = "authentication object."),
	    @Parameter(name = "ediid", description = "The recordid for which user authorization is checked.")})
	@Operation(summary = "Get the authorized token.", description = "Resource returns a JSON if Authorized user.")
	public UserToken token( Authentication authentication,@PathVariable @Valid String ediid)
			throws UnAuthorizedUserException, CustomException, UnAuthenticatedUserException, BadGetwayException {

		AuthenticatedUserDetails userDetails = null;
		try {

			if (authentication == null || authentication.getPrincipal().equals("anonymousUser"))
				throw new UnAuthenticatedUserException(" User is not authenticated to access this resource.");
			logger.info("Get the token for authenticated user.");
			userDetails = uExtract.getUserDetails();

			return jwt.getJWT(userDetails, ediid);
		} catch (UnAuthorizedUserException ex) {
			if (userDetails != null)
				return new UserToken(userDetails, "",ex.getMessage());

			else
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
	@Parameters ({
	    @Parameter(name = "authentication", description = "authentication object."),
	    @Parameter(name = "response", description = "HttpServletResponse .")})
	@Operation(summary = "Get the authorized token.", description = "Resource returns a JSON if Authorized user.")
	
	public ResponseEntity<AuthenticatedUserDetails> login(HttpServletResponse response, Authentication authentication) throws IOException {
		logger.info("Get the authenticated user info.");
//		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication == null) {
			response.sendRedirect("/sso/saml/login");
		} else {
			return new ResponseEntity<>(uExtract.getUserDetails(), HttpStatus.OK);
		}
		return null;
	}

	/**
	 * Exception handling if resource not found
	 * 
	 * @param ex
	 * @param req
	 * @return
	 */
	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorInfo handleStreamingError(ResourceNotFoundException ex, HttpServletRequest req) {
		logger.info("There is an error accessing requested record : " + req.getRequestURI() + "\n  " + ex.getMessage());
		return new ErrorInfo(req.getRequestURI(), 404, "Resource Not Found", req.getMethod());
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

	/**
	 * When an exception occurs in the auth service while connecting
	 * backend or for any other reason.
	 * 
	 * @param ex
	 * @param req
	 * @return
	 */
	@ExceptionHandler(CustomException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorInfo handleStreamingError(CustomException ex, HttpServletRequest req) {
		logger.info("There is an internal error connecting to backend service: " + req.getRequestURI() + "\n  "
				+ ex.getMessage());
		return new ErrorInfo(req.getRequestURI(), 500, "Internal Server Error", req.getMethod());
	}

	/**
	 * When exception is thrown by auth service if the backend metadata
	 * service returns error.
	 * 
	 * @param ex
	 * @param req
	 * @return
	 */
	@ExceptionHandler(BadGetwayException.class)
	@ResponseStatus(HttpStatus.BAD_GATEWAY)
	public ErrorInfo handleStreamingError(BadGetwayException ex, HttpServletRequest req) {
		logger.info("There is an internal error connecting to backend service: " + req.getRequestURI() + "\n  "
				+ ex.getMessage());
		return new ErrorInfo(req.getRequestURI(), 502, "Bad Getway Error", req.getMethod());
	}

}