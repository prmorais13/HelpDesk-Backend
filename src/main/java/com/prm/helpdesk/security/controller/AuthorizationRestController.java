package com.prm.helpdesk.security.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.prm.helpdesk.entities.User;
import com.prm.helpdesk.security.jwt.JwtAuthenticationRequest;
import com.prm.helpdesk.security.jwt.JwtTokenUtil;
import com.prm.helpdesk.security.model.CurrentUser;
import com.prm.helpdesk.services.UserService;

@RestController
@CrossOrigin(origins = "*")
public class AuthorizationRestController {

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private UserService userService;
	
	@PostMapping("/api/auth")
	public ResponseEntity<?> createAuthenticarionToken(@RequestBody JwtAuthenticationRequest authRequest) throws AuthenticationException{
		final Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getSenha())
		);
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		final UserDetails userDetails = this.userDetailsService.loadUserByUsername(authRequest.getEmail());
		final String token = this.jwtTokenUtil.generateToken(userDetails);
		final User user = this.userService.findByEmail(authRequest.getEmail());
		user.setSenha(null);
		return ResponseEntity.ok(new CurrentUser(token, user));
		
	}
	
	@PostMapping("/api/refresh")
	public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request) {
		String token = request.getHeader("authorization");
		String username = this.jwtTokenUtil.getUsernameFromToken(token);
		final User user = this.userService.findByEmail(username);
		
		if (this.jwtTokenUtil.canTokenBeRefreshed(token)) {
			String refreshedToken = this.jwtTokenUtil.refreshToken(token);
			return ResponseEntity.ok(new CurrentUser(refreshedToken, user));
		} else {
			return ResponseEntity.badRequest().body(null);
		}
	}
}
