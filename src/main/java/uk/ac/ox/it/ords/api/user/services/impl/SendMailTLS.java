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

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import uk.ac.ox.it.ords.api.user.model.User;
import uk.ac.ox.it.ords.api.user.services.VerificationEmailService;
import uk.ac.ox.it.ords.security.configuration.MetaConfiguration;

/**
 *
 * @author dave
 */
public class SendMailTLS implements VerificationEmailService {

	private Logger log = LoggerFactory.getLogger(SendMailTLS.class);
	private Properties props;
	private String email;

	public SendMailTLS() {
		props = ConfigurationConverter.getProperties(MetaConfiguration.getConfiguration());
	}

	/* (non-Javadoc)
	 * @see uk.ac.ox.it.ords.api.user.services.VerificationEmailService#sendVerificationMessage(uk.ac.ox.it.ords.api.user.model.User)
	 */
	public void sendVerificationMessage(User user) {
		if (user == null) {
			log.error("Null user here - this is bad!");
			return;
		}
		String messageText = createVerificationMessage(user);
		if ( MetaConfiguration.getConfiguration().getBoolean("mail.send")) sendMail(messageText);
	}
	
	/**
	 * Generate the verification URL the user should use to click through.
	 * @param user
	 * @return
	 */
	protected String getVerificationUrl(User user){
		String link = String.format(props.getProperty("mail.verification.address"), user.getVerificationUuid());
		return link;
	}
	
	protected String createVerificationMessage(User user){
		String messageText = String.format(props.getProperty("mail.verification.message"), user.getName(), getVerificationUrl(user));
		email = user.getEmail();
		if (log.isDebugEnabled()) {
			log.debug("The email I want to send is:" + messageText);
		}
		return messageText;
	}

	private void sendMail(String messageText) {
		if (props.get("mail.smtp.username") == null) {
			log.error("Unable to send emails due to null user");
			return;
		}
		
		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(props.get("mail.smtp.username").toString(), props.get("mail.smtp.password").toString());
			}
		});

		try {
			Message message = new MimeMessage(session);
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(email));
			message.setSubject(props.getProperty("mail.smtp.subject"));
			message.setText(messageText);
			message.setFrom(new InternetAddress("ords@it.ox.ac.uk"));

			Transport.send(message);

			if (log.isDebugEnabled()) {
				log.debug(String.format("Sent email to %s", email));
				log.debug("with content: " + messageText);
			}
		}
		catch (MessagingException e) {
			log.error("Unable to send email to " + email, e);
		}
	}
}
