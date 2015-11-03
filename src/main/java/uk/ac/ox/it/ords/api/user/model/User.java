package uk.ac.ox.it.ords.api.user.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

@Entity
@Table(name = "ordsuser")
public class User {
	
    private String principalName;
    private String principalType;
    
    @Id
    @GeneratedValue
    private int userId;
    
    private String email;
    
    @NotNull
    @Size(min = 2, max = 200)
    private String name = "Unknown";
    
    public enum AccountStatus {PENDING_EMAIL_VERIFICATION, VERIFIED };
    private String status = AccountStatus.PENDING_EMAIL_VERIFICATION.toString();
    
    private String verificationUuid;
    
    public User() {
    	
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Email
    @Column(name = "email", unique = true)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @Column(name = "principalName", unique = true)
    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public String getPrincipalType() {
        return principalType;
    }

    public void setPrincipalType(String principalType) {
        this.principalType = principalType;
    }
    
    public boolean equals(User user) {
        return ( (this.getEmail().equals(user.getEmail())) &&
                (this.getName().equals(user.getName())) );
    }
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVerificationUuid() {
        return verificationUuid;
    }

    public void setVerificationUuid(String verificationUuid) {
        this.verificationUuid = verificationUuid;
    }
}
