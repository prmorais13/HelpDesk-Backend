package com.prm.helpdesk;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.prm.helpdesk.entities.User;
import com.prm.helpdesk.enums.Profile;
import com.prm.helpdesk.repositories.UserRepository;

@SpringBootApplication
public class HelpDeskApplication {

	public static void main(String[] args) {
		SpringApplication.run(HelpDeskApplication.class, args);
	}
	
	@Bean
	CommandLineRunner init(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			this.initUsers(userRepository, passwordEncoder);
		};
	}
	
	private void initUsers(UserRepository userRepository, PasswordEncoder passwordEncoder)  {
		User admin = new User();
		admin.setEmail("pauloroberto@natal.rn.gov.br");
		admin.setSenha(passwordEncoder.encode("Paulo13"));
		admin.setProfiles(Profile.ROLE_ADMIN);
		
		User find = userRepository.findByEmail("pauloroberto@natal.rn.gov.br");
		if (find == null) {
			userRepository.save(admin);
		}
	}
}
