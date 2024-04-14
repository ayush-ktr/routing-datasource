package com.datasource.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datasource.entity.User;
import com.datasource.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	UserRepository userRepository;

	public Integer saveUser(User user) {
		return userRepository.save(user).getPkUserId();
	}
	
	public User fetchUserById(Integer id) {
		return userRepository.findById(id).get();
	}

}
