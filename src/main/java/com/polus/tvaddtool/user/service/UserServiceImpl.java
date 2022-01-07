package com.polus.tvaddtool.user.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polus.tvaddtool.user.dao.UserDao;
import com.polus.tvaddtool.user.pojo.User;

@Transactional
@Service(value = "userServiceImpl")
public class UserServiceImpl implements UserService {

	protected static Logger logger = LogManager.getLogger(UserServiceImpl.class.getName());

	@Autowired
	private UserDao userDao;

	@Override
	public boolean login(User user) {
		return userDao.login(user);
	}

}
