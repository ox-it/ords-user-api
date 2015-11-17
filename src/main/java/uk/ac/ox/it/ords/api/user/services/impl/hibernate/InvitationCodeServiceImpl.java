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
package uk.ac.ox.it.ords.api.user.services.impl.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ox.it.ords.api.user.services.InvitationCodeService;

public class InvitationCodeServiceImpl implements InvitationCodeService {
	
	private static Logger log = LoggerFactory.getLogger(InvitationCodeServiceImpl.class);

	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public InvitationCodeServiceImpl(){
		setSessionFactory (HibernateUtils.getSessionFactory());
	}

	public String getUserByInvitationCode(String code) {
		Session session = this.sessionFactory.getCurrentSession();
		String principalName = null;
		try {
			session.beginTransaction();
			principalName = (String) session.createSQLQuery("SELECT email FROM ordsinvitationtable WHERE uuid = :uuid")
				.setString("uuid", code)
				.uniqueResult();
			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
			log.error("Problem looking up invitation code "+code);
		} finally {
			  HibernateUtils.closeSession();
		}
		return principalName;
	}

}
