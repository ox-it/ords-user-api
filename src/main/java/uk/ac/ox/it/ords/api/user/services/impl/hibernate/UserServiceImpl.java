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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ox.it.ords.api.user.model.OtherUser;
import uk.ac.ox.it.ords.api.user.model.User;
import uk.ac.ox.it.ords.api.user.services.UserAuditService;
import uk.ac.ox.it.ords.api.user.services.UserRoleService;
import uk.ac.ox.it.ords.api.user.services.UserService;
import uk.ac.ox.it.ords.api.user.services.impl.AbstractUserService;
import uk.ac.ox.it.ords.security.model.UserRole;

public class UserServiceImpl extends AbstractUserService implements UserService {

	private static Logger log = LoggerFactory.getLogger(AbstractUserService.class);

	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public UserServiceImpl() {
		setSessionFactory (HibernateUtils.getSessionFactory());
	}
	
	@Override
	public List<OtherUser> getUsers(String q, boolean anywhere) throws Exception {
		
		MatchMode matchMode = MatchMode.START;
		if (anywhere) matchMode = MatchMode.ANYWHERE;

		Session session = this.sessionFactory.getCurrentSession();
		try {
			session.beginTransaction();
			@SuppressWarnings("unchecked")
			List<User> users = (List<User>) session.createCriteria(User.class).add(Restrictions.ilike("name", q, matchMode)).addOrder(Order.asc("name")).setMaxResults(10).list();
			session.getTransaction().commit();
			if (users.size() > 0){
				ArrayList<OtherUser> otherUsers = new ArrayList<OtherUser>();
				for (User user : users){
					otherUsers.add(new OtherUser(user));
				}
				return otherUsers;
			} 
			return null;
		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			  HibernateUtils.closeSession();
		}
	}

	public User getUserByPrincipalName(String principalname) throws Exception {		
		Session session = this.sessionFactory.getCurrentSession();
		try {
			session.beginTransaction();
			@SuppressWarnings("unchecked")
			List<User> users = (List<User>) session.createCriteria(User.class).add(Restrictions.eq("principalName", principalname)).list();
			session.getTransaction().commit();
			if (users.size() == 1){
				return users.get(0);
			} 
			return null;
		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			  HibernateUtils.closeSession();
		}
		
	}

	public User getUserByVerificationId(String verificationId) throws Exception {
		Session session = this.sessionFactory.getCurrentSession();
		try {
			session.beginTransaction();
			@SuppressWarnings("unchecked")
			List<User> users = (List<User>) session.createCriteria(User.class).add(Restrictions.eq("verificationUuid", verificationId)).list();
			session.getTransaction().commit();
			if (users.size() == 1){
				return users.get(0);
			} 
			return null;
		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			  HibernateUtils.closeSession();
		}
	}

	public User getUserByEmailAddress(String email) throws Exception {
		Session session = this.sessionFactory.getCurrentSession();
		try {
			session.beginTransaction();
			@SuppressWarnings("unchecked")
			List<User> users = (List<User>) session.createCriteria(User.class).add(Restrictions.eq("email", email)).list();
			session.getTransaction().commit();
			if (users.size() == 1){
				return users.get(0);
			} 
			return null;
		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			  HibernateUtils.closeSession();
		}
	}

	public User getUser(int userId) throws Exception {
		Session session = this.sessionFactory.getCurrentSession();
		try {
			session.beginTransaction();
			User user = (User) session.get(User.class, userId);
			session.getTransaction().commit();
			return user;
		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			  HibernateUtils.closeSession();
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ox.it.ords.api.user.services.UserService#updateUser(uk.ac.ox.it.ords.api.user.model.User)
	 */
	public void updateUser(User user) throws Exception {
		Session session = this.sessionFactory.getCurrentSession();
		try {
			session.beginTransaction();
			session.update(user);
			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			  HibernateUtils.closeSession();
		}
	}

	public void createUser(User user) throws Exception {
		Session session = this.sessionFactory.getCurrentSession();
		
		//
		// This can't actually happen as we REQUIRE the principal to be the same
		// as that of the subject creating the user
		//
		//if (user.getPrincipalName() == null) {
        //    user.setPrincipalName(user.getEmail());
        //}
		
        if (user.getVerificationUuid() == null) {
            user.setVerificationUuid(UUID.randomUUID().toString());
        }
        
		try {
			session.beginTransaction();
			session.save(user);
			session.getTransaction().commit();
			UserAuditService.Factory.getInstance().createSignupRecord(user);
		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			  HibernateUtils.closeSession();
		}
		
		//
		// Every new user gets the "UnverifiedUser" role.
		UserRole userRole = new UserRole();
		userRole.setPrincipalName(user.getPrincipalName());
		userRole.setRole("unverifieduser");
		UserRoleService.Factory.getInstance().createUserRole(userRole);
	}

	public void deleteUser(User user) throws Exception {

		List<UserRole> userRoles = UserRoleService.Factory.getInstance().getUserRolesForUser(user);
		
		Session session = this.sessionFactory.getCurrentSession();

		try {
			session.beginTransaction();
			session.delete(user);
			session.getTransaction().commit();
			// TODO audit
		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			  HibernateUtils.closeSession();
		}
		
		//
		// Remove all their roles
		//
		for (UserRole userRole : userRoles){
			UserRoleService.Factory.getInstance().deleteUserRole(userRole);
		}
		
	}


}
