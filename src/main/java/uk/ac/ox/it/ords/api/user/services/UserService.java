package uk.ac.ox.it.ords.api.user.services;

import java.util.List;
import java.util.ServiceLoader;

import uk.ac.ox.it.ords.api.user.model.User;
import uk.ac.ox.it.ords.api.user.services.impl.hibernate.UserServiceImpl;

public interface UserService {
	
	public User getUserByPrincipalName(String principalname) throws Exception;
	
	public User getUserByVerificationId(String verificationId) throws Exception;
		
	public User getUserByEmailAddress(String email) throws Exception;
	
	public User getUserByOdbcUser(String odbcuser) throws Exception;
	
	public User getUser(int userId);
	
	public List<User> getUserList();
	
    public boolean updateUser(User user);
    
    public void createUser(User user) throws Exception;
    
    public boolean deleteUser(User user);
    
	/**
	 * Factory for obtaining implementations
	 */
    public static class Factory {
		private static UserService provider;
	    public static UserService getInstance() {
	    	//
	    	// Use the service loader to load an implementation if one is available
	    	// Place a file called uk.ac.ox.oucs.ords.utilities.csv in src/main/resources/META-INF/services
	    	// containing the classname to load as the CsvService implementation. 
	    	// By default we load the Hibernate implementation.
	    	//
	    	if (provider == null){
	    		ServiceLoader<UserService> ldr = ServiceLoader.load(UserService.class);
	    		for (UserService service : ldr) {
	    			// We are only expecting one
	    			provider = service;
	    		}
	    	}
	    	//
	    	// If no service provider is found, use the default
	    	//
	    	if (provider == null){
	    		provider = new UserServiceImpl();
	    	}
	    	
	    	return provider;
	    }
	}
}
