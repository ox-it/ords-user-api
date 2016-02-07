package uk.ac.ox.it.ords.api.user.services.impl;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.jvnet.mock_javamail.Mailbox;

import javax.mail.Message;

import uk.ac.ox.it.ords.api.user.model.ContactRequest;
import uk.ac.ox.it.ords.api.user.model.User;
import uk.ac.ox.it.ords.api.user.services.UserService;

public class ContactRequestServiceImplTest {
	
	@Test
	public void generateContactMessage() throws Exception{	
		User user = new User();
		user.setEmail("scott@test.com");
		user.setName("Scott");
		user.setVerificationUuid("9999A");
		UserService.Factory.getInstance().createUser(user);
		user = UserService.Factory.getInstance().getUserByVerificationId("9999A");
			
		ContactRequestServiceImpl sendMail = new ContactRequestServiceImpl();
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
	public void generateContactMessageNoProperties() throws Exception{	
		User user = new User();
		user.setEmail("scott@test.com");
		user.setName("Scott");
		user.setVerificationUuid("9999A");
		UserService.Factory.getInstance().createUser(user);
		user = UserService.Factory.getInstance().getUserByVerificationId("9999A");
			
		ContactRequestServiceImpl sendMail = new ContactRequestServiceImpl(new Properties());
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
	public void generateContactMessageEmptyString() throws Exception{	
		
		Properties properties = new Properties();
		properties.setProperty("ords.mail.contact.message", "");
		
		User user = new User();
		user.setEmail("scott@test.com");
		user.setName("Scott");
		user.setVerificationUuid("9999A");
		UserService.Factory.getInstance().createUser(user);
		user = UserService.Factory.getInstance().getUserByVerificationId("9999A");
			
		ContactRequestServiceImpl sendMail = new ContactRequestServiceImpl(properties);
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
	public void sendContactMessageTest() throws Exception{
		ContactRequestServiceImpl sendMail = new ContactRequestServiceImpl();
		
		User user = new User();
		user.setEmail("scott@test.com");
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
	
	@Test
	public void sendContactMessageTestNoProperties() throws Exception{
		
		ContactRequestServiceImpl sendMail = new ContactRequestServiceImpl(new Properties());
		
		User user = new User();
		user.setEmail("scott@test.com");
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
	
	@Test
	public void sendMailTest() throws Exception {

		Mailbox.clearAll();

		String subject = "Message from ORDS";
		String body = "You have been sent a message by a user with email address of <test2@test.ords.ox.ac.uk> and name Test. They are interested in your project <TEST> and have sent you the following message\n\nTest";

		Properties properties = new Properties();
		properties.setProperty("ords.mail.send", "true");				
		properties.setProperty("mail.smtp.host", "test.ords.ox.ac.uk");
		properties.setProperty("mail.smtp.from", "test@test.ords.ox.ac.uk");
		properties.setProperty("mail.smtp.username", "test");
		properties.setProperty("mail.smtp.password", "test");
		
		User user = new User();
		user.setEmail("scott@test.ords.ox.ac.uk");
		user.setName("Scott");
		user.setVerificationUuid("9999B");
		UserService.Factory.getInstance().createUser(user);
		user = UserService.Factory.getInstance().getUserByVerificationId("9999B");
		
		ContactRequest request = new ContactRequest();
		request.setEmailAddress("test2@test.ords.ox.ac.uk");
		request.setName("Test");
		request.setUserId(user.getUserId());
		request.setMessage("Test");
		request.setProject("TEST");

		ContactRequestServiceImpl sendMail = new ContactRequestServiceImpl(properties);
		sendMail.sendContactRequest(request, user);

		List<Message> inbox = Mailbox.get("scott@test.ords.ox.ac.uk");
		
		UserService.Factory.getInstance().deleteUser(user);
		
		assertEquals(1, inbox.size());  
		assertEquals(subject, inbox.get(0).getSubject());
		assertEquals(body, inbox.get(0).getContent());
	}
	
}

