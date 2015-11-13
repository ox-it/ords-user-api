package uk.ac.ox.it.ords.api.user.resources;

import static org.junit.Assert.*;

import java.net.URI;

import javax.ws.rs.core.Response;

import org.junit.Test;

import uk.ac.ox.it.ords.api.user.model.User;
import uk.ac.ox.it.ords.api.user.model.User.AccountStatus;

public class UserResourceTest extends AbstractResourceTest {


	@Test
	public void getUserAnon(){
		loginUsingSSO("anonymous", "");
		assertEquals(401, getClient().path("/").get().getStatus());
	}
	
	@Test
	public void getOtherUserUnauthenticated(){
		assertEquals(403, getClient().path("/999").get().getStatus());
	}
	
	@Test
	public void getUser(){
		assertEquals(401, getClient().path("/").get().getStatus());
		
		loginUsingSSO("zaphod", "beeblebrox");
		assertEquals(404, getClient().path("/").get().getStatus());
		logout();
	}
	
	@Test
	public void getUserNonexisting(){
		loginUsingSSO("pingu", "pingu");
		assertEquals(404, getClient().path("/999").get().getStatus());
		logout();
	}
	
	// We only allow self-registration
	@Test
	public void createAnotherUser(){
		loginUsingSSO("pingu", "pingu");
		User user = new User();
		user.setPrincipalName("pinga");
		user.setName("Pinga");		
		assertEquals(400, getClient().path("/").post(user).getStatus());
		logout();
	}

	
	// We only allow self-registration for authenticated users
	@Test
	public void createUserUnauth(){
		User user = new User();
		user.setPrincipalName("pinga");
		user.setName("Pinga");		
		assertEquals(401, getClient().path("/").post(user).getStatus());

		loginUsingSSO("anonymous","");
		assertEquals(401, getClient().path("/").post(user).getStatus());

	}
	
	// We only allow self-registration for authenticated users
	@Test
	public void createAnotherUserUnauth(){
		User user = new User();
		user.setPrincipalName("pinga");
		user.setName("Pinga");		
		assertEquals(401, getClient().path("/").post(user).getStatus());
	}
	
	@Test
	public void createUser(){
		loginUsingSSO("pingu", "pingu");

		User user = new User();
		user.setPrincipalName("pingu");
		user.setName("Pingu");
		user.setEmail("penguin@mailinator.com");
		user.setStatus(User.AccountStatus.VERIFIED.name());
		
		Response response = getClient().path("/").post(user);
		assertEquals(201, response.getStatus());
		URI userUri = response.getLocation();
		
		response =  getClient().path("/").get();
		assertEquals(200, response.getStatus());
		user = response.readEntity(User.class);
		assertEquals("pingu", user.getPrincipalName());
		
		assertEquals(200, getClient().path("/"+user.getUserId()).get().getStatus());
		assertEquals(200, getClient().path(userUri.getPath()).get().getStatus());

		logout();	
		
		// Clean up
		loginUsingSSO("admin", "admin");
		assertEquals(200, getClient().path(userUri.getPath()).delete().getStatus());
		logout();
		
	}
	
	@Test
	public void createUserDuplicateEmail(){
		
		loginUsingSSO("pingu", "pingu");
		User user = new User();
		user.setPrincipalName("pingu");
		user.setName("Pingu");
		user.setEmail("penguin@mailinator.com");
		Response response = getClient().path("/").post(user);
		assertEquals(201, response.getStatus());
		URI user1Uri = response.getLocation();
		logout();	
		
		loginUsingSSO("pingo", "pingo");
		user = new User();
		user.setPrincipalName("pingo");
		user.setName("Pingo");
		user.setEmail("penguin@mailinator.com");
		response = getClient().path("/").post(user);
		assertEquals(409, response.getStatus());
		logout();	
		
		// Clean up
		loginUsingSSO("admin", "admin");
		assertEquals(200, getClient().path(user1Uri.getPath()).delete().getStatus());
		logout();
		
	}
	
	@Test
	public void createUserWithDefaults(){
		loginUsingSSO("pingu", "pingu");

		User user = new User();
		user.setName("Pingu");
		user.setEmail("penguin@mailinator.com");
		
		Response response = getClient().path("/").post(user);
		assertEquals(201, response.getStatus());
		URI userUri = response.getLocation();
		
		response =  getClient().path("/").get();
		assertEquals(200, response.getStatus());
		user = response.readEntity(User.class);
		assertEquals("pingu", user.getPrincipalName());
		assertEquals(AccountStatus.PENDING_EMAIL_VERIFICATION.name(), user.getStatus());

		logout();	
		
		// Clean up
		loginUsingSSO("admin", "admin");
		assertEquals(200, getClient().path(userUri.getPath()).delete().getStatus());
		logout();
		
		loginUsingSSO("pingu", "pingu");

		user = new User();
		user.setName("Pingu");
		user.setPrincipalName("");
		user.setEmail("penguin@mailinator.com");
		
		response = getClient().path("/").post(user);
		assertEquals(201, response.getStatus());
		userUri = response.getLocation();
		
		response =  getClient().path("/").get();
		assertEquals(200, response.getStatus());
		user = response.readEntity(User.class);
		assertEquals("pingu", user.getPrincipalName());
		assertEquals(AccountStatus.PENDING_EMAIL_VERIFICATION.name(), user.getStatus());

		logout();	
		
		// Clean up
		loginUsingSSO("admin", "admin");
		assertEquals(200, getClient().path(userUri.getPath()).delete().getStatus());
		logout();
		
	}
	
	@Test
	public void getUserByNameAndEmail(){
		loginUsingSSO("pinga", "pinga");

		User user = new User();
		user.setPrincipalName("pinga");
		user.setName("Pinga");
		user.setEmail("pinga@mailinator.com");
		
		Response response = getClient().path("/").post(user);
		assertEquals(201, response.getStatus());
		URI userUri = response.getLocation();
		
		response =  getClient().path("/").query("email", "pinga@mailinator.com").get();
		assertEquals(200, response.getStatus());
		user = response.readEntity(User.class);
		assertEquals("Pinga", user.getName());
		assertTrue(user.getUserId() > 0);
		
		response =  getClient().path("/").query("name", "pinga").get();
		assertEquals(200, response.getStatus());
		user = response.readEntity(User.class);
		assertEquals("Pinga", user.getName());
		assertTrue(user.getUserId() > 0);
		
		assertEquals(404, getClient().path("/").query("name", "banana").get().getStatus());
		assertEquals(404, getClient().path("/").query("email", "banana@banana.com").get().getStatus());
			
		logout();
		
		// Clean up
		loginUsingSSO("admin", "admin");
		assertEquals(200, getClient().path(userUri.getPath()).delete().getStatus());
		logout();
		
	}
	
	@Test
	public void deleteUserUnauthenticated(){
		assertEquals(403, getClient().path("/1").delete().getStatus());
	}
	
	@Test
	public void deleteUserNonexisting(){
		loginUsingSSO("admin", "admin");
		assertEquals(404, getClient().path("/999").delete().getStatus());
	}
}
