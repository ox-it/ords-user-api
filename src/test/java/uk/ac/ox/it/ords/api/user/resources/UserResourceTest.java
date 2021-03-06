package uk.ac.ox.it.ords.api.user.resources;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.List;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.junit.Test;

import uk.ac.ox.it.ords.api.user.model.OtherUser;
import uk.ac.ox.it.ords.api.user.model.User;
import uk.ac.ox.it.ords.api.user.model.User.AccountStatus;
import uk.ac.ox.it.ords.api.user.services.UserRoleService;
import uk.ac.ox.it.ords.api.user.services.UserService;
import uk.ac.ox.it.ords.security.configuration.MetaConfiguration;

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
	public void searchOtherUsers(){
		
		loginUsingSSO("pingu", "pingu");

		User user = new User();
		user.setPrincipalName("pingu");
		user.setName("Pingu");
		user.setEmail("penguin@mailinator.com");
		user.setStatus(User.AccountStatus.VERIFIED.name());
		
		Response response = getClient().path("/").post(user);
		assertEquals(201, response.getStatus());
		URI user1 = response.getLocation();
		
		logout();

		// Check we need to be logged in to search
		response = getClient().path("/").query("q", "ping").get();
		assertEquals(403, response.getStatus());
		
		loginUsingSSO("pingu", "pingu");
		
		// No matches
		response = getClient().path("/").query("q", "baa").get();
		assertEquals(404, response.getStatus());
		
		response = getClient().path("/").query("q", "ping").get();
		assertEquals(200, response.getStatus());
		List<OtherUser> users = response.readEntity(new GenericType<List<OtherUser>>() {});
		
		assertEquals(1, users.size());
		logout();
		
		loginUsingSSO("pinga", "pinga");

		user = new User();
		user.setPrincipalName("pinga");
		user.setName("Pinga");
		user.setEmail("penguin2@mailinator.com");
		user.setStatus(User.AccountStatus.VERIFIED.name());
		
		response = getClient().path("/").post(user);
		assertEquals(201, response.getStatus());
		URI user2 = response.getLocation();
		
		logout();
		
		loginUsingSSO("pingu", "pingu");
		response = getClient().path("/").query("q", "ping").get();
		assertEquals(200, response.getStatus());
		users = response.readEntity(new GenericType<List<OtherUser>>() {});
		assertEquals(2, users.size());
		assertEquals("Pinga", users.get(0).getName());
		logout();
		
		loginUsingSSO("pingo", "pingo");

		user = new User();
		user.setPrincipalName("pingo");
		user.setName("Bingo");
		user.setEmail("penguin3@mailinator.com");
		user.setStatus(User.AccountStatus.VERIFIED.name());
		
		response = getClient().path("/").post(user);
		assertEquals(201, response.getStatus());
		URI user3 = response.getLocation();
		
		logout();
		
		loginUsingSSO("pingu", "pingu");
		response = getClient().path("/").query("q", "ping").get();
		assertEquals(200, response.getStatus());
		users = response.readEntity(new GenericType<List<OtherUser>>() {});
		assertEquals(2, users.size());
		assertEquals("Pinga", users.get(0).getName());
		
		response = getClient().path("/").query("q", "bing").get();
		assertEquals(200, response.getStatus());
		users = response.readEntity(new GenericType<List<OtherUser>>() {});
		assertEquals(1, users.size());
		assertEquals("Bingo", users.get(0).getName());
		
		response = getClient().path("/").query("q", "ing").get();
		assertEquals(200, response.getStatus());
		users = response.readEntity(new GenericType<List<OtherUser>>() {});
		assertEquals(3, users.size());
		
		response = getClient().path("/").query("q", "b").get();
		assertEquals(200, response.getStatus());
		users = response.readEntity(new GenericType<List<OtherUser>>() {});
		assertEquals(1, users.size());
		assertEquals("Bingo", users.get(0).getName());
		logout();
		
		//
		// Autocomplete only matches the start of the name, unlike querying
		//
		
		// Check we need a user logged in to conduct a search
		response = getClient().path("/").query("a", "b").get();
		assertEquals(403, response.getStatus());
		
		loginUsingSSO("pingu", "pingu");
		response = getClient().path("/").query("a", "b").get();
		assertEquals(200, response.getStatus());
		users = response.readEntity(new GenericType<List<OtherUser>>() {});
		assertEquals(1, users.size());
		assertEquals("Bingo", users.get(0).getName());
		
		response = getClient().path("/").query("a", "p").get();
		assertEquals(200, response.getStatus());
		users = response.readEntity(new GenericType<List<OtherUser>>() {});
		assertEquals(2, users.size());
		
		response = getClient().path("/").query("a", "ing").get();
		assertEquals(404, response.getStatus());	
		logout();
		
		// Clean up
		loginUsingSSO("admin", "admin");
		assertEquals(200, getClient().path(user1.getPath()).delete().getStatus());
		assertEquals(200, getClient().path(user2.getPath()).delete().getStatus());
		assertEquals(200, getClient().path(user3.getPath()).delete().getStatus());

		logout();
	}
	
	@Test
	public void getUser(){
		assertEquals(401, getClient().path("/").get().getStatus());
		
		loginUsingSSO("zaphod", "beeblebrox");
		assertEquals(404, getClient().path("/").get().getStatus());
		logout();
	}
	
	@Test
	public void getUserUnauth(){
		assertEquals(401, getClient().path("/").get().getStatus());
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
		
		MetaConfiguration.getConfiguration().setProperty("ords.allow.signups", false);
		
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
		
		MetaConfiguration.getConfiguration().setProperty("ords.allow.signups", false);
		
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
		//
		// As user is unverified, we should be restricted from creating anything else
		//
		assertEquals(0,user.getMaximumDatabasesPerProject());
		assertEquals(0, user.getMaximumProjects());
		
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
	public void createUserInvalid(){
		User user = new User();
		user.setName("Pingu");
		user.setPrincipalName("");
		user.setEmail("");
		Response response = getClient().path("/").post(user);
		assertEquals(400, response.getStatus());
		
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
	
	@Test
	public void updateUserUnauth(){
		User user = new User();
		assertEquals(403, getClient().path("/999").put(user).getStatus());
	}
	
	@Test
	public void updateUserNonexisting(){
		User user = new User();
		loginUsingSSO("admin", "admin");
		assertEquals(404, getClient().path("/999").put(user).getStatus());
	}
	
	@Test
	public void resetPassword() throws Exception{
	
		MetaConfiguration.getConfiguration().setProperty("ords.allow.signups", true);
		
		//
		// Register
		//
		User user = new User();
		user.setPrincipalName("pingi");
		user.setName("Pingi");
		user.setEmail("pingi@mailinator.com");
		user.setPasswordRequest("iamapenguin");
		
		Response response = getClient().path("/").post(user);
		assertEquals(201, response.getStatus());
		URI userUri = response.getLocation();
		
		//
		// Login
		//
		login("pingi", "iamapenguin");
		assertEquals(200, getClient().path("/").get().getStatus());
		user = getClient().path("/").get().readEntity(User.class);
		
		//
		// Verify the user so that they are allowed to update
		//
		user.setStatus(User.AccountStatus.VERIFIED.name());
		UserService.Factory.getInstance().updateUser(user);
		UserRoleService.Factory.getInstance().verifyUser(user);
		
		//
		// Reset password
		//
		user.setPasswordRequest("iamnotapenguin");
		assertEquals(200, getClient().path(userUri.getPath()).put(user).getStatus());

		//
		// Login again
		//
		logout();
		login("pingi", "iamnotapenguin");
		assertEquals(200, getClient().path("/").get().getStatus());
		
		//
		// Reset with blank or empty to make sure it never overwrites the token
		//
		user = getClient().path("/").get().readEntity(User.class);
		assertEquals(200, getClient().path(userUri.getPath()).put(user).getStatus());
		logout();
		login("pingi", "iamnotapenguin");
		assertEquals(200, getClient().path("/").get().getStatus());

		logout();
		
		// Clean up
		loginUsingSSO("admin", "admin");
		assertEquals(200, getClient().path(userUri.getPath()).delete().getStatus());
		logout();
		
	}
	
	@Test
	public void updateSelf() throws Exception{
		
		//
		// Register
		//
		loginUsingSSO("pinga", "pinga");

		User user = new User();
		user.setPrincipalName("pinga");
		user.setName("Pinga");
		user.setEmail("pinga@mailinator.com");

		Response response = getClient().path("/").post(user);
		assertEquals(201, response.getStatus());
		URI userUri = response.getLocation();
		// Read back so we have the correct ID
		user = getClient().path("/").get().readEntity(User.class);
		
		//
		// Verify the user so that they are allowed to update
		//
		user.setStatus(User.AccountStatus.VERIFIED.name());
		UserService.Factory.getInstance().updateUser(user);
		UserRoleService.Factory.getInstance().verifyUser(user);
		
		//
		// Set the password token manually, just to check a PUT can't override it
		//
		User vuser = UserService.Factory.getInstance().getUser(user.getUserId());
		vuser.setToken("BANANA");
		UserService.Factory.getInstance().updateUser(vuser);
		vuser = UserService.Factory.getInstance().getUser(user.getUserId());
		assertEquals("BANANA", vuser.getToken());
		
		//
		// Update
		//
		user.setName("Ms P. Penguin");
		assertEquals(200, getClient().path(userUri.getPath()).put(user).getStatus());
		User updatedUser = getClient().path(userUri.getPath()).get().readEntity(User.class);
		assertEquals("Ms P. Penguin", updatedUser.getName());
		
		//
		// Check password token wasn't updated
		//
		User updatedUserComplete = UserService.Factory.getInstance().getUser(updatedUser.getUserId());
		assertEquals("BANANA", updatedUserComplete.getToken());
		
		// Clean up
		loginUsingSSO("admin", "admin");
		assertEquals(200, getClient().path(userUri.getPath()).delete().getStatus());
		logout();
		
	}
	
	@Test
	public void updateInvalid() throws Exception{
		
		//
		// Register
		//
		loginUsingSSO("pinga", "pinga");

		User user = new User();
		user.setPrincipalName("pinga");
		user.setName("Pinga");
		user.setEmail("pinga@mailinator.com");
		
		Response response = getClient().path("/").post(user);
		assertEquals(201, response.getStatus());
		URI userUri = response.getLocation();
		// Read back so we have the correct ID
		user = getClient().path("/").get().readEntity(User.class);
		
		//
		// Verify the user so that they are allowed to update
		//
		user.setStatus(User.AccountStatus.VERIFIED.name());
		UserService.Factory.getInstance().updateUser(user);
		UserRoleService.Factory.getInstance().verifyUser(user);
		
		//
		// Update
		//
		user.setEmail(null);
		assertEquals(400, getClient().path(userUri.getPath()).put(user).getStatus());
		
		// Clean up
		loginUsingSSO("admin", "admin");
		assertEquals(200, getClient().path(userUri.getPath()).delete().getStatus());
		logout();
		
	}
	
	@Test
	public void updateOther() throws Exception{
		
		//
		// Register
		//
		loginUsingSSO("pinga", "pinga");

		User user = new User();
		user.setPrincipalName("pinga");
		user.setName("Pinga");
		user.setEmail("pinga@mailinator.com");
		
		Response response = getClient().path("/").post(user);
		assertEquals(201, response.getStatus());
		URI userUri = response.getLocation();
		// Read back so we have the correct ID
		user = getClient().path("/").get().readEntity(User.class);
		
		//
		// Verify the user so that they are allowed to update
		//
		user.setStatus(User.AccountStatus.VERIFIED.name());
		UserService.Factory.getInstance().updateUser(user);
		UserRoleService.Factory.getInstance().verifyUser(user);
		
		//
		// Update
		//
		logout();
		loginUsingSSO("pingu", "pingu");
		user.setName("Ms P. Penguin");
		assertEquals(403, getClient().path(userUri.getPath()).put(user).getStatus());
		logout();
		
		// Clean up
		loginUsingSSO("admin", "admin");
		assertEquals(200, getClient().path(userUri.getPath()).delete().getStatus());
		logout();
		
	}
	
	@Test
	public void updateSelfUnverified() throws Exception{
		
		//
		// Register
		//
		loginUsingSSO("pinga", "pinga");

		User user = new User();
		user.setPrincipalName("pinga");
		user.setName("Pinga");
		user.setEmail("pinga@mailinator.com");
		
		Response response = getClient().path("/").post(user);
		assertEquals(201, response.getStatus());
		URI userUri = response.getLocation();
		// Read back so we have the correct ID
		user = getClient().path("/").get().readEntity(User.class);
		
		//
		// Update
		//
		user.setName("Ms P. Penguin");
		assertEquals(403, getClient().path(userUri.getPath()).put(user).getStatus());

		// Clean up
		loginUsingSSO("admin", "admin");
		assertEquals(200, getClient().path(userUri.getPath()).delete().getStatus());
		logout();
		
	}
	
	@Test
	public void createUserWithPasswordHash(){
		
		MetaConfiguration.getConfiguration().setProperty("ords.allow.signups", true);
		
		//
		// Register
		//
		User user = new User();
		user.setPrincipalName("pinga");
		user.setName("Pinga");
		user.setEmail("pinga@mailinator.com");
		user.setPasswordRequest("penguin");
		
		Response response = getClient().path("/").post(user);
		assertEquals(201, response.getStatus());
		URI userUri = response.getLocation();
		
		//
		// Login
		//
		login("pinga", "penguin");
		assertEquals(200, getClient().path("/").get().getStatus());
		logout();
		
		//
		// Login using the wrong password
		//
		try {
			login("pinga", "banana");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(401, getClient().path("/").get().getStatus());
		
		// Clean up
		loginUsingSSO("admin", "admin");
		assertEquals(200, getClient().path(userUri.getPath()).delete().getStatus());
		logout();
		
	}
	
	//
	// test that if we don't supply a password, that doesn't make the account
	// open to login with a null password
	//
	@Test
	public void createUserWithNoPassword(){
		
		MetaConfiguration.getConfiguration().setProperty("ords.allow.signups", true);
		
		//
		// Register
		//
		User user = new User();
		user.setPrincipalName("pinga");
		user.setName("Pinga");
		user.setEmail("pinga@mailinator.com");
		user.setPasswordRequest("");
		
		Response response = getClient().path("/").post(user);
		assertEquals(201, response.getStatus());
		URI userUri = response.getLocation();
		// Read back so we have the correct ID
		user = getClient().path("/").get().readEntity(User.class);
		
		logout();
		
		//
		// Login
		//
		try {
			login("pinga", "");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(401, getClient().path("/").get().getStatus());
		
		// Clean up
		loginUsingSSO("admin", "admin");
		assertEquals(200, getClient().path(userUri.getPath()).delete().getStatus());
		logout();
		
	}
	
	@Test
	public void updateSideAttack() throws Exception{
		
		//
		// Register
		//
		loginUsingSSO("pinga", "pinga");

		User user = new User();
		user.setPrincipalName("pinga");
		user.setName("Pinga");
		user.setEmail("pinga@mailinator.com");
		
		Response response = getClient().path("/").post(user);
		assertEquals(201, response.getStatus());
		URI userUri = response.getLocation();
		// Read back so we have the correct ID
		user = getClient().path("/").get().readEntity(User.class);
		
		//
		// Verify the user so that they are allowed to update
		//
		user.setStatus(User.AccountStatus.VERIFIED.name());
		UserService.Factory.getInstance().updateUser(user);
		UserRoleService.Factory.getInstance().verifyUser(user);
		
		//
		// Register User 2
		//
		loginUsingSSO("pingu", "pingu");

		User user2 = new User();
		user2.setPrincipalName("pingu");
		user2.setName("Pingu");
		user2.setEmail("pingu@mailinator.com");
		
		response = getClient().path("/").post(user2);
		assertEquals(201, response.getStatus());
		URI userUri2 = response.getLocation();
		// Read back so we have the correct ID
		user2 = getClient().path("/").get().readEntity(User.class);
		
		//
		// Verify the user so that they are allowed to update
		//
		user2.setStatus(User.AccountStatus.VERIFIED.name());
		UserService.Factory.getInstance().updateUser(user2);
		UserRoleService.Factory.getInstance().verifyUser(user2);
		
		//
		// Update
		//
		user.setName("Ms P. Penguin");
		
		//
		// As Pinga, try to update Pingu's details
		//
		logout();
		loginUsingSSO("pinga", "pinga");
		assertEquals(403, getClient().path(userUri.getPath()).put(user2).getStatus());	
		
		//
		// As Admin, try to update Pingu's details
		//
		logout();
		loginUsingSSO("admin", "admin");
		assertEquals(400, getClient().path(userUri.getPath()).put(user2).getStatus());	
		
		// Clean up
		loginUsingSSO("admin", "admin");
		assertEquals(200, getClient().path(userUri.getPath()).delete().getStatus());
		assertEquals(200, getClient().path(userUri2.getPath()).delete().getStatus());
		logout();
		
	}
	
	
}
