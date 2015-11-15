package uk.ac.ox.it.ords.api.user.services.impl;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.ac.ox.it.ords.api.user.model.User;

public class SendMailTLSTest {
	
	@Test
	public void generateVerificationUrl(){
		
		SendMailTLS sendMail = new SendMailTLS();
		User user = new User();
		user.setEmail("scott.bradley.wilson@gmail.com");
		user.setName("Scott");
		user.setVerificationUuid("9999");
		assertEquals("http://localhost/app/#/verify/9999", sendMail.getVerificationUrl(user));
	}
	
	@Test
	public void generateMessageTest(){
		SendMailTLS sendMail = new SendMailTLS();
		User user = new User();
		user.setEmail("scott.bradley.wilson@gmail.com");
		user.setName("Scott");
		user.setVerificationUuid("9999");
		assertEquals("Hi Scott\n\nIn order to ensure you are able to receive emails from us, please click the following link (if the link below is not clickable, then please copy and paste the URL into a web browser). This will complete the registration process.\n\nhttp://localhost/app/#/verify/9999\n\nThe ORDS Team", sendMail.createVerificationMessage(user));
	}
	
	@Test
	public void sendMessageTest(){
		SendMailTLS sendMail = new SendMailTLS();
		User user = new User();
		user.setEmail("scott.bradley.wilson@gmail.com");
		user.setName("Scott");
		user.setVerificationUuid("9999");
		sendMail.sendVerificationMessage(user);		
	}

}

