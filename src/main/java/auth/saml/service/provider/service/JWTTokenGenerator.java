package auth.saml.service.provider.service;

import java.util.Arrays;

import org.joda.time.DateTime;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import auth.saml.service.provider.exceptions.BadGetwayException;
import auth.saml.service.provider.exceptions.CustomException;
import auth.saml.service.provider.exceptions.UnAuthorizedUserException;
import auth.saml.service.provider.helpers.AuthenticatedUserDetails;



/**
 * This class checks authenticated user and by calling backend service checks
 * whether user is authorized to edit the records. If user is authorized
 * generate token.
 * 
 * @author Deoyani Nandrekar-Heinis
 */
@Component
public class JWTTokenGenerator {

	private Logger logger = LoggerFactory.getLogger(JWTTokenGenerator.class);
	@Value("${oar.mdserver.secret:testsecret}")
	private String mdsecret;

	@Value("${oar.mdserver:}")
	private String mdserver;

	@Value("${jwt.claimname:testsecret}")
	private String JWTClaimName;

	@Value("${jwt.claimvalue:}")
	private String JWTClaimValue;

	@Value("${jwt.secret:}")
	private String JWTSECRET;

	/**
	 * Get the UserToken if user is authorized to edit given record.
	 * 
	 * @param userId Authenticated user
	 * @param ediid  Record identifier
	 * @return UserToken, userid and token
	 * @throws UnAuthorizedUserException
	 * @throws CustomException
	 */
	public UserToken getJWT(AuthenticatedUserDetails userDetails)
			throws UnAuthorizedUserException, BadGetwayException, CustomException {
		logger.info("Get authorized user token.");


		try {
			final DateTime dateTime = DateTime.now();
			// build claims
			JWTClaimsSet.Builder jwtClaimsSetBuilder = new JWTClaimsSet.Builder();
			jwtClaimsSetBuilder.expirationTime(dateTime.plusMinutes(120).toDate());
//			jwtClaimsSetBuilder.claim(JWTClaimName, JWTClaimValue);
			
			jwtClaimsSetBuilder.subject(userDetails.getUserId());
			jwtClaimsSetBuilder.claim("userName",userDetails.getUserName());
			jwtClaimsSetBuilder.claim("userLastName",userDetails.getUserLastName());
			jwtClaimsSetBuilder.claim("userEmail",userDetails.getUserEmail());
//			jwtClaimsSetBuilder.claim("userDiv",userDetails.getUserDiv());
//			jwtClaimsSetBuilder.claim("userDiv",userDetails.getUserDiv());
//			jwtClaimsSetBuilder.claim("userDivNum",userDetails.getUserDivNum());
//			jwtClaimsSetBuilder.claim("userGroup",userDetails.getUserGroup());
//			jwtClaimsSetBuilder.claim("userOU",userDetails.getUserOU());
//					
			
			// signature
			SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), jwtClaimsSetBuilder.build());
			signedJWT.sign(new MACSigner(JWTSECRET));

			return new UserToken(signedJWT.serialize(), userDetails);
		} catch (JOSEException e) {
			logger.error("Unable to generate token for the this user." + e.getMessage());
			throw new UnAuthorizedUserException("Unable to generate token for the this user.");
		}
	}

	
	
	/***
	 * This is older code, can be reused if needed for authorization
	 * Connect to back end metadata service to check whether authenticated user is
	 * authorized to edit the record.
	 * 
	 * @param userId authenticated userid
	 * @param ediid  Record identifier
	 * @return boolean true if the user is authorized.
	 * @throws CustomException
	 * @throws UnAuthorizedUserException
	 */
	public void isAuthorized(AuthenticatedUserDetails userDetails, String ediid) throws UnAuthorizedUserException {
		logger.info("Connect to backend metadata server to get the information.");
		try {
			String uri = "URL to be usedd";
			JSONObject jObject = new JSONObject();
			jObject.put("user",userDetails.getUserId());
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", "Bearer " + mdsecret);
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> requestEntity = new HttpEntity<>(jObject.toString(), headers);
			ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, String.class);

			if (result.getStatusCode().is3xxRedirection() || result.getStatusCode().is5xxServerError()) {
				logger.error("The backend metadata service returned with and error with status:"
						+ result.getStatusCodeValue());
				throw new BadGetwayException(
						"There is an error from backend metadata service. Status:" + result.getStatusCodeValue());
			}
			logger.info("This is response from the backend service." + result.getStatusCodeValue());
			boolean authorized = result.getStatusCode().is2xxSuccessful() ? true : false;
		} catch (final HttpClientErrorException httpClientErrorException) {
			logger.error(
					"4xx Error while requesting authorization from mdserver: " + httpClientErrorException.getMessage());
			if(httpClientErrorException.getStatusCode().value() == 401)
				throw new UnAuthorizedUserException("User does not have permissions to edit this record. ");
			else if(httpClientErrorException.getStatusCode().value() == 404)
				throw new UnAuthorizedUserException("User does not have permissions to edit this record as this record does not exist. ");
			else
				throw new UnAuthorizedUserException("User does not have permissions to edit this record, authorization service returns"+httpClientErrorException.getStatusCode().value());
			
		} catch (HttpServerErrorException httpServerErrorException) {
			logger.error("5xx error while connecting mdserver" + httpServerErrorException.getMessage());
			throw new UnAuthorizedUserException("Unable to get permissions to edit record."
					+ "There is an error getting user authorized as backend Authorization service returns code " + httpServerErrorException.getStatusCode());
		} catch (BadGetwayException exp) {
			logger.error("There is an error response from the backend metadata service." + exp.getMessage());
			throw new UnAuthorizedUserException("Unable to get permissions to edit record. "
					+ "Backend authorization service returns error.");
		} catch (Exception ie) {
			logger.error("There is an exception thrown while connecting to mdserver for authorizing current user."
					+ ie.getMessage());
			throw new UnAuthorizedUserException(
					"There is a problem connecting backend service, can not get permissions for user to edit the record. ");
		}

	}
	
//	//Only for local testing
//	/**
//	 * Get the UserToken if user is authorized to edit given record.
//	 * 
//	 * @param userId Authenticated user
//	 * @param ediid  Record identifier
//	 * @return UserToken, userid and token
//	 * @throws UnAuthorizedUserException
//	 * @throws CustomException
//	 */
//	public UserToken getLocalJWT(AuthenticatedUserDetails userDetails, String ediid)
//			throws UnAuthorizedUserException, BadGetwayException, CustomException {
//		logger.info("Get authorized user token.");
////		isAuthorized(userDetails, ediid);
//
//		try {
//			final DateTime dateTime = DateTime.now();
//			// build claims
//			JWTClaimsSet.Builder jwtClaimsSetBuilder = new JWTClaimsSet.Builder();
//			jwtClaimsSetBuilder.expirationTime(dateTime.plusMinutes(120).toDate());
//			jwtClaimsSetBuilder.claim(JWTClaimName, JWTClaimValue);
//			jwtClaimsSetBuilder.subject(userDetails.getUserEmail() + "|" + ediid);
//
//			// signature
//			SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), jwtClaimsSetBuilder.build());
//			signedJWT.sign(new MACSigner(JWTSECRET));
//
//			return new UserToken( signedJWT.serialize(),"");
//		} catch (JOSEException e) {
//			logger.error("Unable to generate token for the this user." + e.getMessage());
//			throw new UnAuthorizedUserException("Unable to generate token for the this user.");
//		}
//	}

}
