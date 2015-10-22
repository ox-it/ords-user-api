package uk.ac.ox.it.ords.api.user.services;

import java.util.List;

import uk.ac.ox.it.ords.api.user.model.User;
import uk.ac.ox.it.ords.security.model.UserRole;

public interface UserRoleService {
	
	public List<UserRole> getUserRolesForUser(User user);
	public void createUserRole(UserRole userRole) throws Exception;
	public void updateUserRole(UserRole userRole) throws Exception;
	public void deleteUserRole(UserRole userRole) throws Exception;

}
