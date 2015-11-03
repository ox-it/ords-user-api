package uk.ac.ox.it.ords.api.user.services.impl.hibernate;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ox.it.ords.api.user.model.User;
import uk.ac.ox.it.ords.api.user.services.UserRoleService;
import uk.ac.ox.it.ords.security.model.UserRole;

public class UserRoleServiceImpl implements UserRoleService {
	
	
	private static Logger log = LoggerFactory.getLogger(UserRoleServiceImpl.class);

	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public UserRoleServiceImpl() {
		setSessionFactory (HibernateUtils.getSessionFactory());
	}

	public List<UserRole> getUserRolesForUser(User user) throws Exception {
		Session session = this.sessionFactory.getCurrentSession();
		try {
			session.beginTransaction();
			@SuppressWarnings("unchecked")
			List<UserRole> userRoles = session.createCriteria(UserRole.class)
					.add(Restrictions.eq("principalName", user.getPrincipalName()))
					.list();
			session.getTransaction().commit();
			return userRoles;
		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			  HibernateUtils.closeSession();
		}
	}

	public void createUserRole(UserRole userRole) throws Exception {
		Session session = this.sessionFactory.getCurrentSession();
		try {
			session.beginTransaction();
			session.save(userRole);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			log.error("Error creating user role", e);
			session.getTransaction().rollback();
			throw new Exception("Cannot create user role",e);
		} finally {
			  HibernateUtils.closeSession();
		}
	}

	public void deleteUserRole(UserRole userRole) throws Exception {
		Session session = this.sessionFactory.getCurrentSession();
		try {
			session.beginTransaction();
			session.delete(userRole);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			log.error("Error deleting user role", e);
			session.getTransaction().rollback();
			throw new Exception("Cannot delete user role",e);
		} finally {
			  HibernateUtils.closeSession();
		}

	}

	/* (non-Javadoc)
	 * @see uk.ac.ox.it.ords.api.user.services.UserRoleService#verifyUser(uk.ac.ox.it.ords.api.user.model.User)
	 */
	public void verifyUser(User user) throws Exception {
		// 
		// Delete unverified user role
		//
		List<UserRole> roles = getUserRolesForUser(user);
		for (UserRole role : roles){
			if (role.getRole().equals("unverifieduser")){
				deleteUserRole(role);
			}
		}
		
		//
		// Create new role
		//
		UserRole newRole = new UserRole();
		if (user.getPrincipalName().contains("ox.ac.uk")){
			newRole.setRole("localuser");
		}  else  {
			newRole.setRole("user");
		}
		createUserRole(newRole);
		
	}

}
