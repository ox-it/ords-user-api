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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ox.it.ords.api.user.model.User;
import uk.ac.ox.it.ords.api.user.services.UserAuditService;
import uk.ac.ox.it.ords.security.model.Audit;
import uk.ac.ox.it.ords.security.services.AuditService;

public class AuditServiceImpl implements UserAuditService {
	
	Logger log = LoggerFactory.getLogger(AuditServiceImpl.class);

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
		createLoginRecord(user.getPrincipalName());		
	}

	public void createLoginFailedRecord(String message, String userId) {
		Audit audit= new Audit();
		audit.setAuditType(Audit.AuditType.LOGIN_FAILED.name());
		audit.setUserId(userId);
		audit.setMessage(message);
		AuditService.Factory.getInstance().createNewAudit(audit);	
	}

	public void createLoginFailedRecord(String message) {
		Audit audit= new Audit();
		audit.setAuditType(Audit.AuditType.LOGIN_FAILED.name());
		audit.setMessage(message);
		AuditService.Factory.getInstance().createNewAudit(audit);		
	}

	public void createLogoffRecord(String userId) {
		Audit audit= new Audit();
		audit.setAuditType(Audit.AuditType.LOGOFF.name());
		audit.setUserId(userId);
		AuditService.Factory.getInstance().createNewAudit(audit);
	}

	public void createSignupRecord(User user) {
		Audit audit= new Audit();
		audit.setAuditType(Audit.AuditType.SIGNUP.name());
		audit.setUserId(user.getPrincipalName());
		AuditService.Factory.getInstance().createNewAudit(audit);
	}

	@Override
	public void createPasswordChangeRecord(User user) {
		Audit audit = new Audit();
		audit.setAuditType(Audit.AuditType.PASSWORD_CHANGE.name());
		audit.setUserId(user.getPrincipalName());
		AuditService.Factory.getInstance().createNewAudit(audit);
	}
	
	
	
	

}
