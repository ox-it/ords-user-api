package uk.ac.ox.it.ords.api.user.services.impl.hibernate;

import java.util.List;
import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ox.it.ords.api.user.model.User;
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

	public User getUserByPrincipalName(String principalname) throws Exception {		
		Session session = this.sessionFactory.getCurrentSession();
		try {
			session.beginTransaction();
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

	public User getUserByVerificationId(String verificationId) {
		// TODO Auto-generated method stub
		return null;
	}

	public User getUserByEmailAddress(String email) throws Exception {
		Session session = this.sessionFactory.getCurrentSession();
		try {
			session.beginTransaction();
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

	public User getUserByOdbcUser(String odbcuser) {
		// TODO Auto-generated method stub
		return null;
	}

	public User getUser(int userId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<User> getUserList() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean updateUser(User user) {
		// TODO Auto-generated method stub
		return false;
	}

	public void createUser(User user) throws Exception {
		Session session = this.sessionFactory.getCurrentSession();
		
		if (user.getPrincipalName() == null) {
            user.setPrincipalName(user.getEmail());
        }
		
        if (user.getVerificationUuid() == null) {
            user.setVerificationUuid(UUID.randomUUID().toString());
        }
        
		try {
			session.beginTransaction();
			session.save(user);
			session.getTransaction().commit();
			// TODO audit
		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			  HibernateUtils.closeSession();
		}
	}

	public boolean deleteUser(User user) {
		// TODO Auto-generated method stub
		return false;
	}


}
