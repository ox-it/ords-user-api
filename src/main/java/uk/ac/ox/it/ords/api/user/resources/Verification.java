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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;

import uk.ac.ox.it.ords.api.user.model.User;
import uk.ac.ox.it.ords.api.user.services.UserRoleService;
import uk.ac.ox.it.ords.api.user.services.UserService;

/**
 * Process email verification codes
 */
@Api(value="Verification")
@CrossOriginResourceSharing(allowAllOrigins=true)
public class Verification {
	
	@ApiOperation(
			value="Verifies a user's email"	)
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "User successfully verified.", response=uk.ac.ox.it.ords.api.user.model.User.class),
		    @ApiResponse(code = 400, message = "Verification code invalid.")
			})
	// 
	// These are used by upstream gateways; including them here makes it easier to use an API portal
	//
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "Version", value = "API version number", required = false, dataType = "string", paramType = "header"),
	    @ApiImplicitParam(name = "Authorization", value = "API key", required = false, dataType = "string", paramType = "header"),
	  })
	@Path("/verifyemail/{c}")
	@GET
	public Response submitVerificationCode(
			@ApiParam(value = "the verification code to check", required = true) @PathParam("c") final String code
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
