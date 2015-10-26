package uk.ac.ox.it.ords.api.user.model;

import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * This is a minimal version of User returned via
 * the public API, for example the owner of a project
 */
@JsonRootName("user")
public class OtherUser {

	public OtherUser(User user) {
		this.userId = user.getUserId();
		this.name = user.getName();
	}
	
    private int userId;
    private String name = "Unknown";
    
	public int getUserId() {
		return userId;
	}

	public String getName() {
		return name;
	}    

}
