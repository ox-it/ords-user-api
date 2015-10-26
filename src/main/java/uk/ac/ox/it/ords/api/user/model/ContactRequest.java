package uk.ac.ox.it.ords.api.user.model;

/**
 * A request to contact an ords User
 */
public class ContactRequest {

	public ContactRequest() {
	}
	
	// The ORDS user to contact
	int userId;
	
	// The name of the person making contact
	String name;
	// Their message
	String message;
	// Their return email address
	String emailAddress;
	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

}
