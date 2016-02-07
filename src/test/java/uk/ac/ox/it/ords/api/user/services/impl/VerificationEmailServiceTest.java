package uk.ac.ox.it.ords.api.user.services.impl;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Properties;

import javax.mail.Message;

import org.junit.Test;
import org.jvnet.mock_javamail.Mailbox;

import uk.ac.ox.it.ords.api.user.model.User;
import uk.ac.ox.it.ords.api.user.services.UserService;

public class VerificationEmailServiceTest {
	
	@Test
	public void generateVerificationUrl(){
		
		VerificationEmailServiceImpl sendMail = new VerificationEmailServiceImpl();
		User user = new User();
		user.setEmail("scott@test.com");
		user.setName("Scott");
		user.setVerificationUuid("9999");
		assertEquals("http://localhost/app/#/verify/9999", sendMail.getVerificationUrl(user));
	}
	
	@Test
	public void generateVerificationUrlNoProps(){
		
		VerificationEmailServiceImpl sendMail = new VerificationEmailServiceImpl(new Properties());
		User user = new User();
		user.setEmail("scott@test.com");
		user.setName("Scott");
		user.setVerificationUuid("9999");
		assertEquals("http://localhost/app/#/verify/9999", sendMail.getVerificationUrl(user));
	}
	
	@Test(expected = Exception.class)
	public void sendVerificationMessageNoUser() throws Exception{		
		VerificationEmailServiceImpl sendMail = new VerificationEmailServiceImpl();
		sendMail.sendVerificationMessage(null);		
	}
	
	@Test
	public void generateMessageTest(){
		VerificationEmailServiceImpl sendMail = new VerificationEmailServiceImpl();
		User user = new User();
		user.setEmail("scott@test.com");
		user.setName("Scott");
		user.setVerificationUuid("9999");
		assertEquals("Hi Scott\n\nIn order to ensure you are able to receive emails from us, please click the following link (if the link below is not clickable, then please copy and paste the URL into a web browser). This will complete the registration process.\n\nhttp://localhost/app/#/verify/9999\n\nThe ORDS Team", sendMail.createVerificationMessage(user));
	}
	
	@Test
	public void generateVerificationMessageNoProps(){
		VerificationEmailServiceImpl sendMail = new VerificationEmailServiceImpl(new Properties());
		User user = new User();
		user.setEmail("scott@test.com");
		user.setName("Scott");
		user.setVerificationUuid("9999");
		assertEquals("Hi Scott\n\nIn order to ensure you are able to receive emails from us, please click the following link (if the link below is not clickable, then please copy and paste the URL into a web browser). This will complete the registration process.\n\nhttp://localhost/app/#/verify/9999\n\nThe ORDS Team", sendMail.createVerificationMessage(user));
	}
	
	@Test
	public void generateVerificationMessageEmptyString(){
		Properties properties = new Properties();
		properties.setProperty("ords.mail.verification.message", "");			
		properties.setProperty("ords.mail.verification.address", "");			
		
		VerificationEmailServiceImpl sendMail = new VerificationEmailServiceImpl(properties);
		User user = new User();
		user.setEmail("scott@test.com");
		user.setName("Scott");
		user.setVerificationUuid("9999");
		assertEquals("Hi Scott\n\nIn order to ensure you are able to receive emails from us, please click the following link (if the link below is not clickable, then please copy and paste the URL into a web browser). This will complete the registration process.\n\nhttp://localhost/app/#/verify/9999\n\nThe ORDS Team", sendMail.createVerificationMessage(user));
	}
	
	@Test
	public void sendMailTest() throws Exception {

		Mailbox.clearAll();

		String subject = "Message from ORDS";
		String body = "Hi Scott\n\nIn order to ensure you are able to receive emails from us, please click the following link (if the link below is not clickable, then please copy and paste the URL into a web browser). This will complete the registration process.\n\nhttp://localhost/app/#/verify/9999B\n\nThe ORDS Team";

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

		VerificationEmailServiceImpl sendMail = new VerificationEmailServiceImpl(properties);
		sendMail.sendVerificationMessage(user);
		

		List<Message> inbox = Mailbox.get("scott@test.ords.ox.ac.uk");
		
		UserService.Factory.getInstance().deleteUser(user);
		
		assertEquals(1, inbox.size());  
		assertEquals(subject, inbox.get(0).getSubject());
		assertEquals(body, inbox.get(0).getContent());
	}
	
	@Test(expected = Exception.class)
	public void sendMessageNoSettings() throws Exception{
		
		Properties properties = new Properties();
		properties.setProperty("ords.mail.send", "true");
		
		VerificationEmailServiceImpl sendMail = new VerificationEmailServiceImpl(properties);
		User user = new User();
		user.setEmail("scott@test.com");
		user.setName("Scott");
		user.setVerificationUuid("9999");
		sendMail.sendVerificationMessage(user);		
	}

}
