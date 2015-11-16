package uk.ac.ox.it.ords.api.user.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import uk.ac.ox.it.ords.api.user.model.User;
import uk.ac.ox.it.ords.api.user.services.UserRoleService;
import uk.ac.ox.it.ords.api.user.services.UserService;

/**
 * Process email verification codes
 */
public class Verification {
	
	@Path("/verifyemail/{c}")
	@GET
	public Response submitVerificationCode(
			@PathParam("c") final String code
			) throws Exception{
		
		User user = null;
		
		//
		// If there was a code supplied, use this to verify the user
		//
		if (code != null){
			user = UserService.Factory.getInstance().getUserByVerificationId(code);
		}
		
		if (user != null){
			//
			// Set the status of the user to Verified and update their UserRole
			//
			if (user.getStatus().equals(User.AccountStatus.PENDING_EMAIL_VERIFICATION.name())){
				user.setStatus(User.AccountStatus.VERIFIED.name());
				UserService.Factory.getInstance().updateUser(user);
				UserRoleService.Factory.getInstance().verifyUser(user);
				return Response.ok().build();
			}
		}
		return Response.status(400).build();
	}

}
