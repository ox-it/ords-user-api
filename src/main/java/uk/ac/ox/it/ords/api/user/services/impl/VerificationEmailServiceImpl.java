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

import uk.ac.ox.it.ords.api.user.model.User;
import uk.ac.ox.it.ords.api.user.services.VerificationEmailService;

public class VerificationEmailServiceImpl extends SendMailTLS implements
		VerificationEmailService {
	
	private static final String ORDS_MAIL_VERIFICATION_SUBJECT = "Message from ORDS";
	private static final String ORDS_MAIL_VERIFICATION_ADDRESS="http://localhost/app/#/verify/%s";
	private static final String ORDS_MAIL_VERIFICATION_MESSAGE="Hi %s\n\nIn order to ensure you are able to receive emails from us, please click the following link (if the link below is not clickable, then please copy and paste the URL into a web browser). This will complete the registration process.\n\n%s\n\nThe ORDS Team";

	public VerificationEmailServiceImpl(){
		
	}
	
	public VerificationEmailServiceImpl(Properties properties){
		this.props = properties;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ox.it.ords.api.user.services.VerificationEmailService#sendVerificationMessage(uk.ac.ox.it.ords.api.user.model.User)
	 */
	public void sendVerificationMessage(User user) throws Exception{
		
		if (user == null) {
			throw new Exception("Cannot send email to null user");
		}
		
		String messageText = createVerificationMessage(user);
		String subject = null;
		if (props.containsKey("ords.mail.verification.subject")){
			subject = props.getProperty("ords.mail.verification.subject");			
		}
		if (subject == null || subject.isEmpty()){
			subject = ORDS_MAIL_VERIFICATION_SUBJECT;
		}
		
		if (props.containsKey("ords.mail.send")){
			if ( props.get("ords.mail.send").equals("true")) sendMail(subject, messageText);
		}	
	}
	
	/**
	 * Generate the verification URL the user should use to click through.
	 * @param user
	 * @return
	 */
	protected String getVerificationUrl(User user){
		
		String link = null;
		if (props.containsKey("ords.mail.verification.address")){
			link = props.getProperty("ords.mail.verification.address");
		}
		
		if (link == null || link.isEmpty()){
			link = ORDS_MAIL_VERIFICATION_ADDRESS;
		}
		
		link = String.format(link, user.getVerificationUuid());
		return link;
	}
	
	protected String createVerificationMessage(User user){
		String messageText = null;
		
		if (props.containsKey("ords.mail.verification.message")){
			messageText = props.getProperty("ords.mail.verification.message");
		}
		
		if (messageText == null || messageText.isEmpty()){
			messageText = ORDS_MAIL_VERIFICATION_MESSAGE;
		}
		
		messageText = String.format(messageText, user.getName(), getVerificationUrl(user));
		
		email = user.getEmail();
		
		return messageText;
	}
}
