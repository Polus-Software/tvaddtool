package com.polus.tvaddtool.user.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.polus.tvaddtool.user.pojo.User;
import com.polus.tvaddtool.user.service.UserService;

@RestController
public class UserController {

	protected static Logger logger = LogManager.getLogger(UserController.class.getName());

	@Autowired
	private UserService userService;

	@PostMapping("/login")
	public boolean login(@RequestBody User user) {
		logger.info("Requesting for login");
		logger.info("userName : {}", user.getUserName());
		logger.info("password : {}", user.getPassword());
		return userService.login(user);
	}

}
