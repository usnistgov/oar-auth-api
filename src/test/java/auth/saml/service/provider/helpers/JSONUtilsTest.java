package auth.saml.service.provider.helpers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;

import auth.saml.service.provider.exceptions.InvalidInputException;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JSONUtilsTest {

	@Rule
	public  ExpectedException exception = ExpectedException.none();
	

	@Test
	public void isJSONValidTest() throws InvalidInputException {
		String testJson = "{\"title\" : \"New Title Update\",\"description\": \"new description update\"}";

		assertTrue(JSONUtils.isJSONValid(testJson));
		testJson = "{\"title\" : \"New Title Update\",description: \"new description update\"}";
		assertTrue(JSONUtils.isJSONValid(testJson));
		testJson = "{\"jnsfhshdjsjk\"}";
		exception.expect(InvalidInputException.class);
		//assertFalse(JSONUtils.isJSONValid(testJson));
	}

@Test
	public void isValidateInput() throws InvalidInputException {
		
		String testJSON = "{\"title\" : \"New Title Update\",\"description\": \"new description update\"}";
		
		auth.saml.service.provider.exceptions.InvalidInputException exc =  	assertThrows(InvalidInputException.class,
				()-> JSONUtils.validateInput("{\"title\" : \"New Title Update\",\"description\": \"new description update\"}"),
				"Exception validating input JSON against customization service schema"
				);
		assertTrue(exc.getMessage().contains("Exception validating input JSON against customization service schema"));
//		exception.expect(InvalidInputException.class);
//	JSONUtils.validateInput(testJSON);
		testJSON = "{\"title\" : \"New Title Update\",\"description\": [\"new description update\"]}";
		assertTrue(JSONUtils.validateInput(testJSON));
//		testJSON = "{\"jnsfhshdjsjk\"}";
//		exception.expect(InvalidInputException.class);
//		assertFalse(JSONUtils.validateInput(testJSON));
	}

}
