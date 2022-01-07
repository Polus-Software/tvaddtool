package com.polus.tvaddtool.user.service;

import org.springframework.stereotype.Service;

import com.polus.tvaddtool.user.pojo.User;

@Service
public interface UserService {

	public boolean login(User user);

}
