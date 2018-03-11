package com.prm.helpdesk.security.jwt;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.prm.helpdesk.entities.User;
import com.prm.helpdesk.enums.Profile;

public class JwtUserFactory {
	
	public static JwtUser create(User user) {	
		return new JwtUser(user.getId(), user.getEmail(), user.getSenha(),
				mapToGrantedAuthorities(user.getProfiles()));
		
	}

	private static List<GrantedAuthority> mapToGrantedAuthorities(Profile profiles) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority(profiles.toString()));
		return authorities;
	}

}
