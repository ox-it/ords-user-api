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
package uk.ac.ox.it.ords.api.user.services.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ox.it.ords.api.user.model.User;
import uk.ac.ox.it.ords.api.user.services.UserRoleService;
import uk.ac.ox.it.ords.security.configuration.MetaConfiguration;
import uk.ac.ox.it.ords.security.model.UserRole;

public abstract class AbstractUserRoleService implements UserRoleService{
	
	public Logger log = LoggerFactory.getLogger(AbstractUserRoleService.class);
	
	/**
	 * Checks whether the principal name is that of a "local user" - that 
	 * is the domain suffix matches that of the service provider. This is
	 * set in the user.properties configuration file.
	 * @param principalName
	 * @return true if the principal belongs to the host domain.
	 */
	protected boolean isLocalUser(String principalName){
		String localSuffix = MetaConfiguration.getConfiguration().getString("localsuffix");
		
		//
		// We have two possible scenarios:
		// 1. the principal is part of the domain suffix at top level, which means @suffix.com
		// 2. the principal is part of a subdomain, i.e. @something.suffix.com
		//
		if (principalName.endsWith("." + localSuffix)) return true;
		if (principalName.endsWith("@" + localSuffix)) return true;
		
		return false;
	}
	

	/* (non-Javadoc)
	 * @see uk.ac.ox.it.ords.api.user.services.UserRoleService#verifyUser(uk.ac.ox.it.ords.api.user.model.User)
	 */
	public void verifyUser(User user) throws Exception {
		// 
		// Delete unverified user role
		//
		List<UserRole> roles = getUserRolesForUser(user);
		for (UserRole role : roles){
			if (role.getRole().equals("unverifieduser")){
				deleteUserRole(role);
			}
		}
		
		//
		// Create new role
		//
		UserRole newRole = new UserRole();
		newRole.setPrincipalName(user.getPrincipalName());
		if (isLocalUser(user.getPrincipalName())){
			newRole.setRole("localuser");
		}  else  {
			newRole.setRole("user");
		}
		createUserRole(newRole);
		
	}


}
