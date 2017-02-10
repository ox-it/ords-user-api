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

import uk.ac.ox.it.ords.api.user.model.User;
import uk.ac.ox.it.ords.api.user.permissions.UserPermissionSets;
import uk.ac.ox.it.ords.api.user.services.UserService;
import uk.ac.ox.it.ords.security.model.Permission;
import uk.ac.ox.it.ords.security.services.PermissionsService;

public abstract class AbstractUserService implements UserService {
	
	/* (non-Javadoc)
	 * @see uk.ac.ox.it.ords.api.user.services.UserService#init()
	 */
	public void init() throws Exception {
		PermissionsService service = PermissionsService.Factory.getInstance();
		

		//
		// "Anonymous" can View public projects
		//
		for (String permission : UserPermissionSets.getPermissionsForAnonymous()){
			Permission permissionObject = new Permission();
			permissionObject.setRole("anonymous");
			permissionObject.setPermission(permission);
			service.createPermission(permissionObject);
		}
		

		//
		// "Unverified" can View public projects and verify their account
		//
		for (String permission : UserPermissionSets.getPermissionsForUnverifiedUser()){
			Permission permissionObject = new Permission();
			permissionObject.setRole("unverified");
			permissionObject.setPermission(permission);
			service.createPermission(permissionObject);
		}
		
		//
		// Anyone with the "User" role can contribute to existing projects
		//
		for (String permission : UserPermissionSets.getPermissionsForUser()){
			Permission permissionObject = new Permission();
			permissionObject.setRole("user");
			permissionObject.setPermission(permission);
			service.createPermission(permissionObject);
		}
		
		//
		// Anyone with the "Administrator" role can create new full
		// projects and upgrade projects to full, and update any
		// user projects
		//
		for (String permission : UserPermissionSets.getPermissionsForSysadmin()){
			Permission permissionObject = new Permission();
			permissionObject.setRole("administrator");
			permissionObject.setPermission(permission);
			service.createPermission(permissionObject);
		}

		//
		// "Anonymous" can View public projects
		//
		for (String permission : UserPermissionSets.getPermissionsForAnonymous()){
			Permission permissionObject = new Permission();
			permissionObject.setRole("anonymous");
			permissionObject.setPermission(permission);
			service.createPermission(permissionObject);
		}
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ox.it.ords.api.user.services.UserService#validate(uk.ac.ox.it.ords.api.user.model.User)
	 */
	public boolean validate(User user) {
		
		if (user.getEmail() == null || user.getEmail().trim().length() == 0){
			return false;
		}
		
		return true;
	}	

}
