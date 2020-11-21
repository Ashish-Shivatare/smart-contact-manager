package com.smart.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smart.doa.UserRepository;
import com.smart.entities.User;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public User getUserByUserName(String username) {
		return this.userRepository.getUserByUserName(username);
	}
	
	@Override
	public void save(User user) {
		this.userRepository.save(user);
	}
	
}
