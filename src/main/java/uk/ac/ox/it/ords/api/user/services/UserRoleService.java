package uk.ac.ox.it.ords.api.user.services;

import java.util.List;
import java.util.ServiceLoader;

import uk.ac.ox.it.ords.api.user.model.User;
import uk.ac.ox.it.ords.api.user.services.impl.hibernate.UserRoleServiceImpl;
import uk.ac.ox.it.ords.security.model.UserRole;

public interface UserRoleService {
	
	public List<UserRole> getUserRolesForUser(User user) throws Exception;
	public void createUserRole(UserRole userRole) throws Exception;
	public void updateUserRole(UserRole userRole) throws Exception;
	public void deleteUserRole(UserRole userRole) throws Exception;
	
	/**
	 * Factory for obtaining implementations
	 */
    public static class Factory {
		private static UserRoleService provider;
	    public static UserRoleService getInstance() {
	    	//
	    	// Use the service loader to load an implementation if one is available
	    	// Place a file called uk.ac.ox.oucs.ords.utilities.csv in src/main/resources/META-INF/services
	    	// containing the classname to load as the CsvService implementation. 
	    	// By default we load the Hibernate implementation.
	    	//
	    	if (provider == null){
	    		ServiceLoader<UserRoleService> ldr = ServiceLoader.load(UserRoleService.class);
	    		for (UserRoleService service : ldr) {
	    			// We are only expecting one
	    			provider = service;
	    		}
	    	}
	    	//
	    	// If no service provider is found, use the default
	    	//
	    	if (provider == null){
	    		provider = new UserRoleServiceImpl();
	    	}
	    	
	    	return provider;
	    }
	}

}
