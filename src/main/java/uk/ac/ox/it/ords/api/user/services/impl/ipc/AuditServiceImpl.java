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
package uk.ac.ox.it.ords.api.user.services.impl.ipc;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ox.it.ords.api.user.model.User;
import uk.ac.ox.it.ords.api.user.services.UserAuditService;
import uk.ac.ox.it.ords.security.model.Audit;
import uk.ac.ox.it.ords.security.services.AuditService;

public class AuditServiceImpl implements UserAuditService {
	
	Logger log = LoggerFactory.getLogger(AuditServiceImpl.class);
	
	private String getPrincipalName(){
		try {
			if (SecurityUtils.getSubject() == null || SecurityUtils.getSubject().getPrincipal() == null) return "Unauthenticated";
			return SecurityUtils.getSubject().getPrincipal().toString();
		} catch (UnavailableSecurityManagerException e) {
			log.warn("Audit being called with no valid security context. This is probably caused by being called from unit tests");
			return "Security Manager Not Configured";
		}
	}

	public void createNotAuthRecord(String request, String user) {
		Audit audit= new Audit();
		audit.setAuditType(Audit.AuditType.GENERIC_NOTAUTH.name());
		audit.setUserId(user);
		audit.setMessage(request);
		AuditService.Factory.getInstance().createNewAudit(audit);
	}

	public void createLoginRecord(String userId) {
		Audit audit= new Audit();
		audit.setAuditType(Audit.AuditType.LOGIN.name());
		audit.setUserId(userId);
		AuditService.Factory.getInstance().createNewAudit(audit);	
	}

	public void createLoginRecord(User user) {
		// TODO Auto-generated method stub
		
	}

	public void createLoginFailedRecord(String message, String userId) {
		// TODO Auto-generated method stub
		
	}

	public void createLoginFailedRecord(String message) {
		// TODO Auto-generated method stub
		
	}

	public void createLogoffRecord(String userId) {
		// TODO Auto-generated method stub
		
	}

	public void createSignupRecord(User user) {
		// TODO Auto-generated method stub
		
	}
	
	

}
