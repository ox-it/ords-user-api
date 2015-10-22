package uk.ac.ox.it.ords.api.user.resources;

import static org.junit.Assert.*;

import javax.ws.rs.core.Response;

import org.junit.Test;

import uk.ac.ox.it.ords.api.user.model.User;

public class UserResourceTest extends AbstractResourceTest {

	@Test
	public void getUser(){
		assertEquals(401, getClient().path("/").get().getStatus());
		
		loginUsingSSO("zaphod", "beeblebrox");
		assertEquals(404, getClient().path("/").get().getStatus());
		logout();
	}
	
	@Test
	public void createUser(){
		loginUsingSSO("pingu", "pingu");

		User user = new User();
		user.setPrincipalName("pingu");
		user.setName("Pingu");
		user.setStatus(User.AccountStatus.AUTHORIZED.name());
		
		Response response = getClient().path("/").post(user);
		assertEquals(201, response.getStatus());
		
		response =  getClient().path("/").get();
		assertEquals(200, response.getStatus());
		user = response.readEntity(User.class);
		assertEquals("pingu", user.getPrincipalName());
		assertTrue(user.getUserId() > 0);
		
		logout();
		
	}
	

}
