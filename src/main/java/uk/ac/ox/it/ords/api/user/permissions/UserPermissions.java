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
