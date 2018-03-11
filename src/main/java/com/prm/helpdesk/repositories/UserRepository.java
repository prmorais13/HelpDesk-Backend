package com.prm.helpdesk.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.prm.helpdesk.entities.User;

@Repository
public interface UserRepository extends MongoRepository<User, String>{
	
	User findByEmail(String email);

	// User findOne(String id);

}
