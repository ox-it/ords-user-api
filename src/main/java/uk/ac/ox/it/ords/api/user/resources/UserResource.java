package uk.ac.ox.it.ords.api.user.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.shiro.SecurityUtils;

import uk.ac.ox.it.ords.api.user.model.OtherUser;
import uk.ac.ox.it.ords.api.user.model.User;
import uk.ac.ox.it.ords.api.user.permissions.UserPermissions;
import uk.ac.ox.it.ords.api.user.services.AuditService;
import uk.ac.ox.it.ords.api.user.services.UserService;

public class UserResource {
	
	@Path("/{id}")
	@DELETE
	public Response deleteUser(
			@PathParam("id") final int id
			) throws Exception{
		
		//
		// Check auth
		//
		if (!SecurityUtils.getSubject().isPermitted(UserPermissions.USER_DELETE_ALL)){
			AuditService.Factory.getInstance().createNotAuthRecord(UserPermissions.USER_DELETE_ALL, String.valueOf(id));
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
	
	@Path("/")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createUser(
			User user,
			@Context UriInfo uriInfo
			) throws Exception{
		
		//
		// We must have a logged-in user
		//
		if (!SecurityUtils.getSubject().isAuthenticated()){
			return Response.status(401).build();
		}
		
		//
		// The special anonymous user provides public permissions;
		// if this is the principal, we treat them as unauthenticated
		//
		if (SecurityUtils.getSubject().getPrincipal().equals("anonymous")){
			AuditService.Factory.getInstance().createNotAuthRecord("User:query", "anonymous");
			return Response.status(401).build();
		}
		
		//
		// If the principal is null, set it to the current subject
		//
		if (user.getPrincipalName() == null || user.getPrincipalName() == ""){
			user.setPrincipalName(SecurityUtils.getSubject().getPrincipal().toString());
		}
		
		//
		// Check the principal name and the user match
		//
		if (!user.getPrincipalName().equals(SecurityUtils.getSubject().getPrincipal())){
			return Response.status(400).build();
		}
		
		//
		// Override with new user defaults
		//
		user.setStatus(User.AccountStatus.PENDING_EMAIL_VERIFICATION.name());
		user.setPrincipalType("");
		
		//
		// Create the user
		//
		UserService.Factory.getInstance().createUser(user);
		
	    UriBuilder builder = uriInfo.getAbsolutePathBuilder();
	    builder.path(Integer.toString(user.getUserId()));
	    return Response.created(builder.build()).build();
		
	}
	
	/**
	 * Get the User for the current Subject
	 * @return
	 * @throws Exception 
	 */
	@Path("/")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(
			@QueryParam("name") final String name,
			@QueryParam("email") final String email
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
			AuditService.Factory.getInstance().createNotAuthRecord("User:query", null);
			return Response.status(401).build();
		}
		
		String principalName = SecurityUtils.getSubject().getPrincipal().toString();
		
		User user = UserService.Factory.getInstance().getUserByPrincipalName(principalName);

		//
		// There is no user for the principal.
		//
		if (user == null){
			//
			// The special anonymous user provides public permissions;
			// if this is the principal, we treat them as unauthenticated
			//
			if (principalName.equals("anonymous")){
				AuditService.Factory.getInstance().createNotAuthRecord("User:query", "anonymous");
				return Response.status(401).build();
			}
			
			//
			// Otherwise, the user may never have registered even though
			// they have authenticated. In which case we need to prompt
			// the client to register them.
			//
			return Response.status(404).build();
		}
	
		return Response.ok(user).build();
	}
	
	/**
	 * Get the User for a given id
	 * @return
	 * @throws Exception 
	 */
	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(
			@PathParam("id") final int id
			) throws Exception{
		
		//
		// The user is logged in
		// 
		if (!SecurityUtils.getSubject().isAuthenticated()){
			AuditService.Factory.getInstance().createNotAuthRecord("User:query", "anonymous");
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
