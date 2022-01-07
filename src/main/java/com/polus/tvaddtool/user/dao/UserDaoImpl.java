package com.polus.tvaddtool.user.dao;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polus.tvaddtool.user.pojo.User;

@Transactional
@Service(value = "userDao")
public class UserDaoImpl implements UserDao {

	protected static Logger logger = LogManager.getLogger(UserDaoImpl.class.getName());

	@Autowired
	private HibernateTemplate hibernateTemplate;

	@Override
	public boolean login(User user) {
		Session session = null;
		try {
			session = hibernateTemplate.getSessionFactory().openSession();
			String hqlQuery = "SELECT COUNT(*) FROM User U WHERE U.userName = :userName AND U.password = :password";
			Query query = session.createQuery(hqlQuery);
			query.setParameter("userName", user.getUserName());
			query.setParameter("password", user.getPassword());
			Long count = (Long) query.getSingleResult();
			if (count > 0) {
				return true;
			}
		} catch (Exception e) {
			logger.error("Exception in login : {} ", e.getMessage());
		} finally {
			if (session != null)
				session.close();
		}
		return false;
	}

}
