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
package uk.ac.ox.it.ords.api.user.services.impl;

import java.util.Properties;

import org.apache.commons.configuration.ConfigurationConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ox.it.ords.api.user.model.ContactRequest;
import uk.ac.ox.it.ords.api.user.model.User;
import uk.ac.ox.it.ords.api.user.services.ContactRequestService;
import uk.ac.ox.it.ords.security.configuration.MetaConfiguration;

public class ContactRequestServiceImpl extends SendMailTLS implements ContactRequestService {
	
	private Logger log = LoggerFactory.getLogger(ContactRequestServiceImpl.class);

	public ContactRequestServiceImpl() {
		props = ConfigurationConverter.getProperties(MetaConfiguration.getConfiguration());
	}
	
	public ContactRequestServiceImpl(Properties properties){
		props = properties;
	}
	
	//
	// Default messages - these can be overridden in user.properties
	//
	private static final String ORDS_MAIL_CONTACT_SUBJECT="Message from ORDS";
	private static final String ORDS_MAIL_CONTACT_MESSAGE="You have been sent a message by a user with email address of <%s> and name %s. They are interested in your project <%s> and have sent you the following message\n\n%s";
	
	/* (non-Javadoc)
	 * @see uk.ac.ox.it.ords.api.user.services.ContactRequestService#sendContactRequest(uk.ac.ox.it.ords.api.user.model.ContactRequest)
	 */
	@Override
	public void sendContactRequest(ContactRequest contactRequest, User user) throws Exception{
		
		
		String messageText = createContactRequestMessage(contactRequest);
		
		String subject = null;
		if (props.containsKey("ords.mail.contact.subject")){
			subject = props.getProperty("ords.mail.contact.subject");			
		}
		if (subject == null || subject.isEmpty()){
			subject = ORDS_MAIL_CONTACT_SUBJECT;
		}
		
		email = user.getEmail();
		
		if (props.containsKey("ords.mail.send")){
			if ( props.get("ords.mail.send").equals("true")){
				sendMail(subject, messageText);
			}
		}
	}
	
	protected String createContactRequestMessage(ContactRequest contactRequest){
		
		String messageText = null;
		
		if (props.containsKey("ords.mail.contact.message")){
			messageText = props.getProperty("ords.mail.contact.message");
		}
		
		if (messageText == null || messageText.isEmpty()){
			messageText = ORDS_MAIL_CONTACT_MESSAGE;
		}
				
		messageText = String.format(messageText, contactRequest.getEmailAddress(), contactRequest.getName(), contactRequest.getProject(), contactRequest.getMessage());
		return messageText;
	}

}
