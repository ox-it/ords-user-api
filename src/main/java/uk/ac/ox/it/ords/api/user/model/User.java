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
package uk.ac.ox.it.ords.api.user.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

import uk.ac.ox.it.ords.security.services.RestrictionsService;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "ordsuser")
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
	
    private String principalName;
    private String principalType;
    private String odbcUser;
    
    @Transient
    private String passwordRequest;
    
    //
    // Password hash if using built-in authentication
    //
    @JsonIgnore 
    private String token;
    
    @Id
    @GeneratedValue
    private int userId;
    
    private String email;
    
    @NotNull
    @Size(min = 2, max = 200)
    private String name = "Unknown";
    
    public enum AccountStatus {PENDING_EMAIL_VERIFICATION, VERIFIED };
    private String status = AccountStatus.PENDING_EMAIL_VERIFICATION.toString();
    
    @JsonIgnore 
    private String verificationUuid;
    
    
    public static String ODBC_CALCULATED_NAME_SUFFIX = "_ords";

    
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
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @JsonIgnore 
    public String getVerificationUuid() {
        return verificationUuid;
    }

    @JsonIgnore 
    public void setVerificationUuid(String verificationUuid) {
        this.verificationUuid = verificationUuid;
    }
    
    
    public String getOdbcUser() {
		return odbcUser;
	}
    
    public String calculateOdbcUserForOrds() {
    	return getOdbcUser() + ODBC_CALCULATED_NAME_SUFFIX;
    }
    
    public static String getOdbcNameFromCalculatedName(String calculatedName) {
        if (calculatedName == null) {
            return null;
        }
        if (calculatedName.endsWith(ODBC_CALCULATED_NAME_SUFFIX)) {
            return calculatedName.substring(0, ODBC_CALCULATED_NAME_SUFFIX.length()-1);
        }
        return calculatedName;
    }


	public void setOdbcUser(String odbcUser) {
		this.odbcUser = odbcUser;
	}

    @JsonIgnore 
	public String getToken() {
		return token;
	}
    
    @JsonIgnore 
	public void setToken(String token) {
		this.token = token;
	}
  
	public String getPasswordRequest() {
		return passwordRequest;
	}

	public void setPasswordRequest(String passwordRequest) {
		this.passwordRequest = passwordRequest;
	}
	
	//
	// Returns information on restrictions for UI
	//
    @JsonProperty
	public int getMaximumProjects(){
		return RestrictionsService.Factory.getInstance().getMaximumNumberOfLiveProjects();
	}
    @JsonProperty
	public int getMaximumDatabasesPerProject(){
		return RestrictionsService.Factory.getInstance().getMaximumDatabasesPerProject();
	}
    @JsonProperty
	public int getMaximumDatasetsPerDatabase(){
		return RestrictionsService.Factory.getInstance().getMqxiumumDatasetsPerDatabase();
	}
    @JsonProperty
	public int getMaximumUploadSize(){
		return RestrictionsService.Factory.getInstance().getMaximumUploadSize();
	}

}
