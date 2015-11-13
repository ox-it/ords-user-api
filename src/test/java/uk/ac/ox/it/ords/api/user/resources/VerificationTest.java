package uk.ac.ox.it.ords.api.user.resources;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import javax.ws.rs.core.Response;

import org.junit.Test;

import uk.ac.ox.it.ords.api.user.model.User;
import uk.ac.ox.it.ords.api.user.services.UserService;

public class VerificationTest extends AbstractResourceTest {

	@Test
	public void verifyInvalid(){
		assertEquals(400, getClient().path("verifyemail/9999").get().getStatus());
	}
	
	
	@Test
	public void createUserWithoutEmailAddress(){
		loginUsingSSO("pingu", "pingu");
		User user = new User();
		user.setPrincipalName("pingu");
		user.setName("Pingu");
		Response response = getClient().path("/").post(user);
		assertEquals(400, response.getStatus());
	}
	
	@Test
	public void createAndVerifyUser() throws Exception{
		loginUsingSSO("pingu", "pingu");
		User user = new User();
		user.setPrincipalName("pingu");
		user.setName("Pingu");
		user.setEmail("penguin@mailinator.com");
		
		Response response = getClient().path("/").post(user);
		assertEquals(201, response.getStatus());
		URI userUri = response.getLocation();
		
		response =  getClient().path("/").get();
		assertEquals(200, response.getStatus());
		user = response.readEntity(User.class);
		assertEquals("pingu", user.getPrincipalName());
		assertEquals("PENDING_EMAIL_VERIFICATION", user.getStatus());

		
		assertEquals(200, getClient().path("/"+user.getUserId()).get().getStatus());
		assertEquals(200, getClient().path(userUri.getPath()).get().getStatus());
		logout();	
		
		//
		// Read the code from the DB
		//
		String code = UserService.Factory.getInstance().getUser(user.getUserId()).getVerificationUuid();
		
		//
		// Now verify
		//
		response = getClient().path("/verifyemail/"+code).get();
		assertEquals(200, response.getStatus());
		
		//
		// Now GET again
		//
		loginUsingSSO("pingu", "pingu");
		response =  getClient().path("/").get();
		assertEquals(200, response.getStatus());
		user = response.readEntity(User.class);
		assertEquals("VERIFIED", user.getStatus());
		logout();
				
		// Clean up
		loginUsingSSO("admin", "admin");
		assertEquals(200, getClient().path(userUri.getPath()).delete().getStatus());
		logout();
		
	}
}
