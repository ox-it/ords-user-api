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

import uk.ac.ox.it.ords.api.user.model.ContactRequest;
import uk.ac.ox.it.ords.api.user.model.User;
import uk.ac.ox.it.ords.api.user.services.impl.SendMailTLS;

public interface ContactRequestService {
	
	public void sendContactRequest(ContactRequest contactRequest, User user);
	
	/**
	 * Factory for obtaining implementations
	 */
    public static class Factory {
		private static ContactRequestService provider;
	    public static ContactRequestService getInstance() {
	    	//
	    	// Use the service loader to load an implementation if one is available
	    	// Place a file called uk.ac.ox.oucs.ords.utilities.csv in src/main/resources/META-INF/services
	    	// containing the classname to load as the CsvService implementation. 
	    	// By default we load the Hibernate implementation.
	    	//
	    	if (provider == null){
	    		ServiceLoader<ContactRequestService> ldr = ServiceLoader.load(ContactRequestService.class);
	    		for (ContactRequestService service : ldr) {
	    			// We are only expecting one
	    			provider = service;
	    		}
	    	}
	    	//
	    	// If no service provider is found, use the default
	    	//
	    	if (provider == null){
	    		provider = new SendMailTLS();
	    	}
	    	
	    	return provider;
	    }
	}


}
