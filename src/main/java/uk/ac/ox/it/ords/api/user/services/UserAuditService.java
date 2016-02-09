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

import java.util.ServiceLoader;

import uk.ac.ox.it.ords.api.user.model.User;
import uk.ac.ox.it.ords.api.user.services.impl.ipc.AuditServiceImpl;

public interface UserAuditService {
	
    /**
     * Create audit message that the user is not authorised to perform a specific action
     * @param request the action that is not authorised
     * @param userId a representation of the user id
     */
    public void createNotAuthRecord(String request, String userId);
	
    /*
     * Login and signup
     */
    
    /**
     * Create an audit event when a user logs in to ORDS.
     * If there is no record of the user, an audit record stating than the user could not be found.
     * @param userId the id of the user logging in to ORDS
     */
	public void createLoginRecord(String userId);
	
    /**
     * Create an audit event when a user logs in to ORDS.
     * If there is no record of the user, an audit record stating than the user could not be found.
     * @param user the user object logging in to ORDS
     */
    public void createLoginRecord(User user);
    
    /**
     * Record a message that the user login attempt was unsuccessful.
     * @param message A message to be written with the audit record.
     * @param userId the id of the user.
     */
	public void createLoginFailedRecord(String message, String userId);
    
    /**
     * Record a message that the user login attempt was unsuccessful.
     * @param message A message to be written with the audit record.
     */
    public void createLoginFailedRecord(String message);

    /**
     * Record a message that the user logged out of ORDS.
     * @param userId the id of the user.
     */
	public void createLogoffRecord(String userId);

    /**
     * Create a signup audit record for an user. No record will be created for null input.
     * @param user The user object to sign up. 
     */
	public void createSignupRecord(User user);
	
	/**
	 * Create a record that a user requested a password change
	 * @param user
	 */
	public void createPasswordChangeRecord(User user);
	
	/**
	 * Factory for obtaining implementations
	 */
    public static class Factory {
		private static UserAuditService provider;
	    public static UserAuditService getInstance() {
	    	//
	    	// Use the service loader to load an implementation if one is available
	    	// Place a file called uk.ac.ox.oucs.ords.utilities.csv in src/main/resources/META-INF/services
	    	// containing the classname to load as the CsvService implementation. 
	    	// By default we load the Hibernate implementation.
	    	//
	    	if (provider == null){
	    		ServiceLoader<UserAuditService> ldr = ServiceLoader.load(UserAuditService.class);
	    		for (UserAuditService service : ldr) {
	    			// We are only expecting one
	    			provider = service;
	    		}
	    	}
	    	//
	    	// If no service provider is found, use the default
	    	//
	    	if (provider == null){
	    		provider = new AuditServiceImpl();
	    	}
	    	
	    	return provider;
	    }
	}


}
