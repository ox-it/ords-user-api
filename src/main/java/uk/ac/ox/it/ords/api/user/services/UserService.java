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
package uk.ac.ox.it.ords.api.user.services;

import java.util.List;
import java.util.ServiceLoader;

import uk.ac.ox.it.ords.api.user.model.OtherUser;
import uk.ac.ox.it.ords.api.user.model.User;
import uk.ac.ox.it.ords.api.user.services.impl.hibernate.UserServiceImpl;

public interface UserService {
	
	/**
	 * Initialise the service, setting up default roles and permissions
	 */
	public void init() throws Exception;
	
	public boolean validate(User user);
	
	public List<OtherUser> getUsers(String q, boolean anywhere) throws Exception;
	
	public User getUserByPrincipalName(String principalname) throws Exception;
	
	public User getUserByVerificationId(String verificationId) throws Exception;
		
	public User getUserByEmailAddress(String email) throws Exception;
		
	public User getUser(int userId) throws Exception;
		
    public void updateUser(User user) throws Exception;
    
    public void createUser(User user) throws Exception;
    
    public void deleteUser(User user) throws Exception;
    
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
