package uk.ac.ox.it.ords.api.user.permissions;

import uk.ac.ox.it.ords.security.permissions.Permissions;

public class UserPermissions extends Permissions {
	
	//
	// Permissions relating to users
	//
	public static final String USER_CREATE_SELF = "user:create-self";
	public static final String USER_VERIFY_SELF = "user:verify-self";
	public static final String USER_DELETE_ALL = "user:delete:*";
	public static final String USER_MODIFY_ALL = "user:modify:*";
	public static final String USER_MODIFY_SELF = "user:modify-self";
	
}
