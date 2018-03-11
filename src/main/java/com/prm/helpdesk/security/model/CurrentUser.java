package com.prm.helpdesk.security.model;

import com.prm.helpdesk.entities.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class CurrentUser {
	private String token;
	private User user;
}
