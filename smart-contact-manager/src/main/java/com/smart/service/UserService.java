package com.smart.service;

import com.smart.entities.User;

public interface UserService {
	User getUserByUserName(String username);
	void save(User user);
}
