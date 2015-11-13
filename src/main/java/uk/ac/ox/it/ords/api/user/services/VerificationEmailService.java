package uk.ac.ox.it.ords.api.user.services;

import java.util.ServiceLoader;

import uk.ac.ox.it.ords.api.user.model.User;
import uk.ac.ox.it.ords.api.user.services.impl.SendMailTLS;

public interface VerificationEmailService {

	public void sendVerificationMessage(User user);
	
	/**
	 * Factory for obtaining implementations
	 */
    public static class Factory {
		private static VerificationEmailService provider;
	    public static VerificationEmailService getInstance() {
	    	//
	    	// Use the service loader to load an implementation if one is available
	    	// Place a file called uk.ac.ox.oucs.ords.utilities.csv in src/main/resources/META-INF/services
	    	// containing the classname to load as the CsvService implementation. 
	    	// By default we load the Hibernate implementation.
	    	//
	    	if (provider == null){
	    		ServiceLoader<VerificationEmailService> ldr = ServiceLoader.load(VerificationEmailService.class);
	    		for (VerificationEmailService service : ldr) {
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
