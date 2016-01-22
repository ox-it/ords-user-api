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
package uk.ac.ox.it.ords.api.user.permissions;

import java.util.ArrayList;
import java.util.List;

public class UserPermissionSets {

	public static List<String> getPermissionsForAnonymous(){
		ArrayList<String> permissions = new ArrayList<String>();
		permissions.add(UserPermissions.USER_CREATE_SELF);
		permissions.add(UserPermissions.PROJECT_VIEW_PUBLIC);
		return permissions;
	}

	public static List<String> getPermissionsForUnverifiedUser(){
		ArrayList<String> permissions = new ArrayList<String>();
		permissions.add(UserPermissions.USER_VERIFY_SELF);
		permissions.add(UserPermissions.PROJECT_VIEW_PUBLIC);
		return permissions;
	}
	
	
	public static List<String> getPermissionsForUser(){
		ArrayList<String> permissions = new ArrayList<String>();
		permissions.add(UserPermissions.USER_CREATE_SELF);
		permissions.add(UserPermissions.USER_MODIFY_SELF);
		permissions.add(UserPermissions.PROJECT_VIEW_PUBLIC);
		return permissions;
	}
	
	public static List<String> getPermissionsForLocalUser(){
		ArrayList<String> permissions = new ArrayList<String>();
		permissions.add(UserPermissions.USER_CREATE_SELF);
		permissions.add(UserPermissions.USER_MODIFY_SELF);
		permissions.add(UserPermissions.PROJECT_VIEW_PUBLIC);
		permissions.add(UserPermissions.PROJECT_CREATE);
		return permissions;
	}
	
	public static List<String> getPermissionsForSysadmin(){
		ArrayList<String> permissions = new ArrayList<String>();
		
		permissions.add(UserPermissions.USER_MODIFY_ALL);
		permissions.add(UserPermissions.USER_DELETE_ALL);
		
		permissions.add(UserPermissions.PROJECT_CREATE_FULL);
		permissions.add(UserPermissions.PROJECT_UPGRADE);
		permissions.add(UserPermissions.PROJECT_MODIFY_ALL);
		permissions.add(UserPermissions.PROJECT_VIEW_ALL);
		return permissions;
	}
}
