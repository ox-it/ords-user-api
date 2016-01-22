package uk.ac.ox.it.ords.api.user.services.impl;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.ac.ox.it.ords.api.user.model.ContactRequest;
import uk.ac.ox.it.ords.api.user.model.User;
import uk.ac.ox.it.ords.api.user.services.UserService;

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
	public void generateContactMessageTest() throws Exception{	
		User user = new User();
		user.setEmail("scott.bradley.wilson@gmail.com");
		user.setName("Scott");
		user.setVerificationUuid("9999A");
		UserService.Factory.getInstance().createUser(user);
		user = UserService.Factory.getInstance().getUserByVerificationId("9999A");
			
		SendMailTLS sendMail = new SendMailTLS();
		ContactRequest contactRequest = new ContactRequest();
		contactRequest.setEmailAddress("penguin@mailinator.com");
		contactRequest.setMessage("Pls talk 2 me");
		contactRequest.setName("Penguin");
		contactRequest.setProject("A Project");
		contactRequest.setUserId(user.getUserId());
		assertEquals("You have been sent a message by a user with email address of <penguin@mailinator.com> and name Penguin. They are interested in your project <A Project> and have sent you the following message\n\nPls talk 2 me", sendMail.createContactRequestMessage(contactRequest));

		UserService.Factory.getInstance().deleteUser(user);
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
	
	@Test
	public void sendContactMessageTest() throws Exception{
		SendMailTLS sendMail = new SendMailTLS();
		
		User user = new User();
		user.setEmail("scott.bradley.wilson@gmail.com");
		user.setName("Scott");
		user.setVerificationUuid("9999B");
		UserService.Factory.getInstance().createUser(user);
		user = UserService.Factory.getInstance().getUserByVerificationId("9999B");
		
		ContactRequest contactRequest = new ContactRequest();
		contactRequest.setEmailAddress("penguin@mailinator.com");
		contactRequest.setMessage("Pls talk 2 me");
		contactRequest.setName("Penguin");
		contactRequest.setProject("A Project");
		contactRequest.setUserId(user.getUserId());
		assertEquals("You have been sent a message by a user with email address of <penguin@mailinator.com> and name Penguin. They are interested in your project <A Project> and have sent you the following message\n\nPls talk 2 me", sendMail.createContactRequestMessage(contactRequest));

		
		sendMail.sendContactRequest(contactRequest, user);	
		
		UserService.Factory.getInstance().deleteUser(user);

	}

}

