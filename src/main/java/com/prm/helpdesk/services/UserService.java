package com.prm.helpdesk.services;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.prm.helpdesk.entities.User;

@Component
public interface UserService {
	
	User findByEmail(String email);
	
	User createOrUpdate(User usuario);
	
	User findById(String id);
	
	void delete(String id);
	
	Page<User> findAll(int page, int count);

}
