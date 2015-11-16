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

import uk.ac.ox.it.ords.api.user.services.impl.hibernate.InvitationCodeServiceImpl;

public interface InvitationCodeService {
	
	/**
	 * Lookup the given Invitation code and retrieve the matching principal, or
	 * null if there is no match found
	 * @param code
	 * @return either the principal invited with the code, or null
	 * TODO this is quite a hack as it crosses the boundary between the User and
	 * Project APIs; we should investigate a cleaner design
	 */
	public String getUserByInvitationCode(String code);
	
	/**
	 * Factory for obtaining implementations
	 */
    public static class Factory {
		private static InvitationCodeService provider;
	    public static InvitationCodeService getInstance() {
	    	//
	    	// Use the service loader to load an implementation if one is available
	    	// Place a file called uk.ac.ox.oucs.ords.utilities.csv in src/main/resources/META-INF/services
	    	// containing the classname to load as the CsvService implementation. 
	    	// By default we load the Hibernate implementation.
	    	//
	    	if (provider == null){
	    		ServiceLoader<InvitationCodeService> ldr = ServiceLoader.load(InvitationCodeService.class);
	    		for (InvitationCodeService service : ldr) {
	    			// We are only expecting one
	    			provider = service;
	    		}
	    	}
	    	//
	    	// If no service provider is found, use the default
	    	//
	    	if (provider == null){
	    		provider = new InvitationCodeServiceImpl();
	    	}
	    	
	    	return provider;
	    }
	}

}
