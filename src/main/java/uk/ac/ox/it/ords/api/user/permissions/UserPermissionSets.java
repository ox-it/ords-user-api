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
	
	public static List<String> getPermissionsForUser(){
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
