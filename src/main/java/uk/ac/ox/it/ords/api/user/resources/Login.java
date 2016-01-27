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
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;

import uk.ac.ox.it.ords.api.user.services.UserAuditService;

/**
 * Performs a login with a user name and password using the Shiro security manager.
 */
public class Login {
	
	/**
	 * Perform a login with this user
	 * @param username
	 * @param password
	 * @param rememberMe
	 * @return
	 */
	@Path("/login")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response login(
			@FormParam("username") String username, 
			@FormParam("password") String password,
			@FormParam("rememberMe") String rememberMe
	){		
		    try {
		    	//
		    	// Login succeeded - create an audit record and return 200
		    	//
			    AuthenticationToken token =  new UsernamePasswordToken(username, password);
				SecurityUtils.getSubject().login(token);
				UserAuditService.Factory.getInstance().createLoginRecord(username);
				return Response.ok().build();
			} catch (AuthenticationException e) {
				//
				// Login failed - create an audit record and return 401
				//
				UserAuditService.Factory.getInstance().createLoginFailedRecord("", username);
				return Response.status(401).build();
			}
		}

}
