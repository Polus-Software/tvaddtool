package com.polus.tvaddtool.user.dao;

import org.springframework.stereotype.Service;

import com.polus.tvaddtool.user.pojo.User;

@Service
public interface UserDao {

	public boolean login(User user);

}
