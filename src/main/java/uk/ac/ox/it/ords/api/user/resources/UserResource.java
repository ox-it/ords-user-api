/*
 * Copyright 2015 University of Oxford
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ox.it.ords.api.user.resources;

import java.net.URI;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;

import javax.annotation.PostConstruct;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.credential.DefaultPasswordService;

import uk.ac.ox.it.ords.api.user.model.OtherUser;
import uk.ac.ox.it.ords.api.user.model.User;
import uk.ac.ox.it.ords.api.user.permissions.UserPermissions;
import uk.ac.ox.it.ords.api.user.services.InvitationCodeService;
import uk.ac.ox.it.ords.api.user.services.UserAuditService;
import uk.ac.ox.it.ords.api.user.services.UserRoleService;
import uk.ac.ox.it.ords.api.user.services.UserService;
import uk.ac.ox.it.ords.api.user.services.VerificationEmailService;
import uk.ac.ox.it.ords.security.configuration.MetaConfiguration;

@Api(value="User")
@CrossOriginResourceSharing(allowAllOrigins=true)
public class UserResource {
	
	/**
	 * Before the resource can be used, carry out initialisation steps
	 * @throws Exception 
	 */
	@PostConstruct
	public void init() throws Exception{
		UserService.Factory.getInstance().init();
	}
	
	@ApiOperation(
			value="Updates a user", 
			notes=""
			)
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "User successfully updated."),
		    @ApiResponse(code = 400, message = "Invalid User supplied."),
		    @ApiResponse(code = 403, message = "Not authorized to update this user."),
		    @ApiResponse(code = 404, message = "User not found.")
	})
	// 
	// These are used by upstream gateways; including them here makes it easier to use an API portal
	//
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "Version", value = "API version number", required = false, dataType = "string", paramType = "header"),
	    @ApiImplicitParam(name = "Authorization", value = "API key", required = false, dataType = "string", paramType = "header"),
	})
	@Path("/{id}")
	@PUT
	public Response updateUser(
			@PathParam("id") final int id,
			User user
			) throws Exception {
		
		//
		// Check auth
		//
		
		// No user
		if (SecurityUtils.getSubject().getPrincipal() == null){
			return Response.status(403).build();	
		}
		
		// Modify-self
		if (SecurityUtils.getSubject().getPrincipal().equals(user.getPrincipalName())){
			if (!SecurityUtils.getSubject().isPermitted(UserPermissions.USER_MODIFY_SELF)){
				return Response.status(403).build();
			}
		} else {
			// Modify-other
			if (!SecurityUtils.getSubject().isPermitted(UserPermissions.USER_MODIFY_ALL)){
				return Response.status(403).build();
			}
		}

		
		//
		// Does the original User object exist?
		//
		User originalUser = UserService.Factory.getInstance().getUser(id);
		if (originalUser == null){
			return Response.status(404).build();
		}
		
		//
		// Check for side-attack
		//
		if (user.getUserId() != id){
			return Response.status(400).build();
		}
		
		//
		// Validate
		//
		if (!UserService.Factory.getInstance().validate(user)){
			return Response.status(400).build();			
		}
		
		//
		// Update the User
		//
		UserService.Factory.getInstance().updateUser(user);
		return Response.ok().build();		
	}
	
	@ApiOperation(value="Deletes a user")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "User successfully deleted."),
		    @ApiResponse(code = 400, message = "Invalid user ID supplied."),
		    @ApiResponse(code = 403, message = "Not authorized to delete this user."),
		    @ApiResponse(code = 404, message = "User not found or has already been deleted.")
	})
	// 
	// These are used by upstream gateways; including them here makes it easier to use an API portal
	//
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "Version", value = "API version number", required = false, dataType = "string", paramType = "header"),
	    @ApiImplicitParam(name = "Authorization", value = "API key", required = false, dataType = "string", paramType = "header"),
	  })
	@Path("/{id}")
	@DELETE
	public Response deleteUser(
			@PathParam("id") final int id
			) throws Exception{
		
		//
		// Check auth
		//
		if (!SecurityUtils.getSubject().isPermitted(UserPermissions.USER_DELETE_ALL)){
			UserAuditService.Factory.getInstance().createNotAuthRecord(UserPermissions.USER_DELETE_ALL, String.valueOf(id));
			return Response.status(403).build();
		}
		
		User user = UserService.Factory.getInstance().getUser(id);
		
		//
		// There is no user.
		//
		if (user == null){
			return Response.status(404).build();
		}
		
		UserService.Factory.getInstance().deleteUser(user);
		
		return Response.ok().build();
		
	}
	
	@ApiOperation(value="Creates a user")
	@ApiResponses(value = { 
			@ApiResponse(code = 201, message = "User successfully created.",
					responseHeaders = @ResponseHeader(name = "Location", description = "The URI of the user", response = URI.class)
					),
		    @ApiResponse(code = 400, message = "Invalid user details."),
		    @ApiResponse(code = 403, message = "Not authorized to create a user.")
	})
	// 
	// These are used by upstream gateways; including them here makes it easier to use an API portal
	//
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "Version", value = "API version number", required = false, dataType = "string", paramType = "header"),
	    @ApiImplicitParam(name = "Authorization", value = "API key", required = false, dataType = "string", paramType = "header"),
	  })
	@Path("/")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createUser(
			User user,
			@Context UriInfo uriInfo,
			@ApiParam(value = "Invitation code to verify, if user is being created in response to an invitation from an existing user", required = false) @QueryParam("i") final String invitationCode
			) throws Exception{
		
		//
		// We must have a logged-in user unless we allow sign ups 
		//
		if (!MetaConfiguration.getConfiguration().getBoolean("ords.allow.signups")){
			if (!SecurityUtils.getSubject().isAuthenticated()){
				return Response.status(401).build();
			}
		}
		
		//
		// The special anonymous user provides public permissions;
		// if this is the principal, we treat them as unauthenticated
		//
		if (SecurityUtils.getSubject().getPrincipal() != null){
			if (SecurityUtils.getSubject().getPrincipal().equals("anonymous")){
				UserAuditService.Factory.getInstance().createNotAuthRecord("User:query", "anonymous");
				return Response.status(401).build();
			}
		}
		
		//
		// If the principal is null, set it to the current subject
		//
		if (SecurityUtils.getSubject().getPrincipal() != null){
			if (user.getPrincipalName() == null || user.getPrincipalName() == ""){
				user.setPrincipalName(SecurityUtils.getSubject().getPrincipal().toString());
			}
		}
		
		//
		// If the principal is null and the subject are null, set the principal to be the email address
		//
		if (user.getPrincipalName() == null || user.getPrincipalName() == ""){
			user.setPrincipalName(user.getEmail());
		}		
		
		//
		// Check the principal name and the user match if we're not supporting self-sign-up
		//
		if (!MetaConfiguration.getConfiguration().getBoolean("ords.allow.signups")){
			if (!user.getPrincipalName().equals(SecurityUtils.getSubject().getPrincipal())){
				return Response.status(400).build();
			}
		}
		
		//
		// Check there isn't another user with this email address.
		//
		if (UserService.Factory.getInstance().getUserByEmailAddress(user.getEmail()) != null){
			return Response.status(409).build();
		}
		
		//
		// Override with new user defaults
		//
		user.setStatus(User.AccountStatus.PENDING_EMAIL_VERIFICATION.name());
		user.setPrincipalType("");
		
		// Generate the OdbcUser name from the principal
		String odbcUser = user.getPrincipalName().replace("@", "").replace(".", "");
		user.setOdbcUser(odbcUser);

		//
		// Generate a password hash, if a password was requested
		//
		if (user.getPasswordRequest() != null && !user.getPasswordRequest().isEmpty()){
			DefaultPasswordService passwordService = new DefaultPasswordService();
			user.setToken(passwordService.encryptPassword(user.getPasswordRequest()));
		}
		//
		// If we have an invitation code, look it up
		//
		if (invitationCode != null){
			
			//
			// Get the code
			//
			String principalName = InvitationCodeService.Factory.getInstance().getUserByInvitationCode(invitationCode);
			
			//
			// No match
			//
			if (principalName == null){
				return Response.status(400).build();
			}
			
			//
			// Does the principal name matching the code also match the principal name of the user we're creating?
			//
			if (!principalName.equals(user.getPrincipalName())){				
				return Response.status(400).build();
			}
			
			//
			// Create the user, and verify their role at the same time
			//
			user.setStatus(User.AccountStatus.VERIFIED.name());
			UserService.Factory.getInstance().createUser(user);
			UserRoleService.Factory.getInstance().verifyUser(user);
			
		} else {
			
			//
			// Validate the request
			//
			if (!UserService.Factory.getInstance().validate(user)){
				return Response.status(400).build();
			}
			
			//
			// Create the user
			//
			UserService.Factory.getInstance().createUser(user);
			
			//
			// Send verification email
			//
			VerificationEmailService.Factory.getInstance().sendVerificationMessage(user);
		}

		//
		// Return 201 with location of User object
		//
		UserAuditService.Factory.getInstance().createSignupRecord(user);
	    UriBuilder builder = uriInfo.getAbsolutePathBuilder();
	    builder.path(Integer.toString(user.getUserId()));
	    return Response.created(builder.build()).build();
		
	}
	
	/**
	 * Get the User for the current Subject
	 * @return
	 * @throws Exception 
	 */
	@ApiOperation(
			value="Gets a user",
			notes= "If called without parameters, this returns the currently logged-in user; you can also call this method with either a user name or an email address to return a specific user. Note that when used as a query, a more limited subset of User data is returned."
	)
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "User successfully returned.", response=uk.ac.ox.it.ords.api.user.model.User.class),
		    @ApiResponse(code = 403, message = "Not authorized to view user."),
		    @ApiResponse(code = 404, message = "User not found.")
			})
	// 
	// These are used by upstream gateways; including them here makes it easier to use an API portal
	//
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "Version", value = "API version number", required = false, dataType = "string", paramType = "header"),
	    @ApiImplicitParam(name = "Authorization", value = "API key", required = false, dataType = "string", paramType = "header"),
	  })
	@Path("/")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(
			@ApiParam(value = "optional user principal name to search for", required = false) @QueryParam("name") final String name,
			@ApiParam(value = "optional user email address to search for", required = false) @QueryParam("email") final String email
			) throws Exception{
		
		//
		// If this is a query by principal name...
		//
		if (name != null){
			User user = UserService.Factory.getInstance().getUserByPrincipalName(name);

			if (user == null){
				return Response.status(404).build();
			}
			
			//
			// We need to filter the output, so we wrap it in the OtherUser return class
			//
			OtherUser otherUser = new OtherUser(user);
			return Response.ok(otherUser).build();
		}
		
		//
		// Or a query by email name...
		//
		if (email != null){
			User user = UserService.Factory.getInstance().getUserByEmailAddress(email);
			
			if (user == null){
				return Response.status(404).build();
			}
			
			//
			// We need to filter the output, so we wrap it in the OtherUser return class
			//
			OtherUser otherUser = new OtherUser(user);
			return Response.ok(otherUser).build();
			
		}
		
		//
		// Otherwise we return the current Subject
		//
		
		//
		// The user is logged in
		// 
		if (!SecurityUtils.getSubject().isAuthenticated()){
			UserAuditService.Factory.getInstance().createNotAuthRecord("User:query", null);
			return Response.status(401).build();
		}
		
		String principalName = SecurityUtils.getSubject().getPrincipal().toString();
		
		User user = UserService.Factory.getInstance().getUserByPrincipalName(principalName);

		//
		// There is no user for the principal.
		//
		if (user == null){
			
			//
			// If the principal name is null we aren't logged in 
			//
			if (principalName == null){
				return Response.status(401).build();				
			}
			
			//
			// The special anonymous user provides public permissions;
			// if this is the principal, we treat them as unauthenticated
			//
			if (principalName.equals("anonymous")){
				UserAuditService.Factory.getInstance().createNotAuthRecord("User:query", "anonymous");
				return Response.status(401).build();
			}
			
			//
			// Otherwise, the user may never have registered even though
			// they have authenticated. In which case we need to prompt
			// the client to register them.
			//
			System.out.println("we're looking for.." + principalName);
			return Response.status(404).build();
		}
	
		return Response.ok(user).build();
	}
	
	/**
	 * Get the User for a given id
	 * @return
	 * @throws Exception 
	 */
	@ApiOperation(value="Gets a specified user")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "User successfully returned.", response=uk.ac.ox.it.ords.api.user.model.User.class),
		    @ApiResponse(code = 400, message = "Invalid ID supplied."),
		    @ApiResponse(code = 403, message = "Not authorized to view user."),
		    @ApiResponse(code = 404, message = "User not found.")
			})
	// 
	// These are used by upstream gateways; including them here makes it easier to use an API portal
	//
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "Version", value = "API version number", required = false, dataType = "string", paramType = "header"),
	    @ApiImplicitParam(name = "Authorization", value = "API key", required = false, dataType = "string", paramType = "header"),
	  })
	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(
			@ApiParam(value = "the ID of the user", required = true) @PathParam("id") final int id
			) throws Exception{
		
		//
		// The user is logged in
		// 
		if (!SecurityUtils.getSubject().isAuthenticated()){
			UserAuditService.Factory.getInstance().createNotAuthRecord("User:query", "anonymous");
			return Response.status(403).build();
		}
				
		User user = UserService.Factory.getInstance().getUser(id);
		
		//
		// There is no user for the principal.
		//
		if (user == null){
			return Response.status(404).build();
		}
		
		//
		// We need to filter the output, so we wrap it in the OtherUser return class
		//
		OtherUser otherUser = new OtherUser(user);
	
		return Response.ok(otherUser).build();
	}

}
