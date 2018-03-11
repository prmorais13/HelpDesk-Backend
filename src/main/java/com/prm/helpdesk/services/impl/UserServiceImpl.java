package com.prm.helpdesk.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.prm.helpdesk.entities.User;
import com.prm.helpdesk.repositories.UserRepository;
import com.prm.helpdesk.services.UserService;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public User findByEmail(String email) {
		return this.userRepository.findByEmail(email);
	}

	@Override
	public User createOrUpdate(User usuario) {
		return this.userRepository.save(usuario);
	}

	@Override
	public User findById(String id) {
		return this.userRepository.findOne(id);
		// return null;
	}

	@Override
	public void delete(String id) {
		// this.userRepository.deleteById(id);
		this.userRepository.delete(id);

	}

	// @SuppressWarnings("deprecation")
	@Override
	public Page<User> findAll(int page, int count) {
		Pageable pages = new PageRequest(page, count);
		return this.userRepository.findAll(pages);
	}

}
