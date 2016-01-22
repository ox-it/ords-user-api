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

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;

import uk.ac.ox.it.ords.api.user.model.ContactRequest;
import uk.ac.ox.it.ords.api.user.model.User;
import uk.ac.ox.it.ords.api.user.services.ContactRequestService;
import uk.ac.ox.it.ords.api.user.services.UserService;

@Api(value="Contact Request")
@CrossOriginResourceSharing(allowAllOrigins=true)
public class ContactRequestResource {
	
	@ApiOperation(value="Creates a contact request")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Request successfully created and sent."),
		    @ApiResponse(code = 400, message = "Invalid contact details."),
	})
	// 
	// These are used by upstream gateways; including them here makes it easier to use an API portal
	//
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "Version", value = "API version number", required = false, dataType = "string", paramType = "header"),
	    @ApiImplicitParam(name = "Authorization", value = "API key", required = false, dataType = "string", paramType = "header"),
	  })
	@Path("/contact")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createContactRequest(
			ContactRequest contactRequest
			) throws Exception{
		
		User user = UserService.Factory.getInstance().getUser(contactRequest.getUserId());
		
		if (user == null){
			return Response.status(400).build();
		}
		
		ContactRequestService.Factory.getInstance().sendContactRequest(contactRequest, user);
		
		return Response.ok().build();
	}

}
