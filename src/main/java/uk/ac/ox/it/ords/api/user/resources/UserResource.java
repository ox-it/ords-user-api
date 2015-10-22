package uk.ac.ox.it.ords.api.user.resources;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.shiro.SecurityUtils;

import uk.ac.ox.it.ords.api.user.model.User;
import uk.ac.ox.it.ords.api.user.permissions.UserPermissions;
import uk.ac.ox.it.ords.api.user.services.UserService;

public class UserResource {
	
	@Path("/")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createUser(
			User user,
			@Context UriInfo uriInfo
			) throws Exception{
		
		//
		// Check auth
		//
		if (!SecurityUtils.getSubject().isPermitted(UserPermissions.USER_CREATE_SELF)){
			throw new ForbiddenException();
		}
		
		//
		// Check the principal name and the user match
		//
		if (!user.getPrincipalName().equals(SecurityUtils.getSubject().getPrincipal())){
			throw new BadRequestException();
		}
		
		//
		// Override with new user defaults
		//
		user.setStatus(User.AccountStatus.PENDING_EMAIL_VERIFICATION.name());
		user.setVerificationUuid("test");
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
	public Response getUser() throws Exception{
		
		//
		// The user is logged in
		// 
		if (!SecurityUtils.getSubject().isAuthenticated()){
			throw new NotAuthorizedException(Response.status( 401 ).build());
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
				throw new NotAuthorizedException(Response.status( 401 ).build());				
			}
			
			//
			// Otherwise, the user may never have registered even though
			// they have authenticated. In which case we need to prompt
			// the client to register them.
			//
			throw new NotFoundException();
		}
	
		return Response.ok(user).build();
	}

}
