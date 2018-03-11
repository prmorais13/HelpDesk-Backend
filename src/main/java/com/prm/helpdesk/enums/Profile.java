	package com.prm.helpdesk.enums;

public enum Profile {
	
	ROLE_ADMIN ("Administrador"),
	ROLE_CLIENTE ("Cliente"),
	ROLE_TECNICO ("Técnico");
	
	Profile(String descricao) {
		this.descricao = descricao;
	}
	
	private String descricao;
	
	public String getDescricao() {
		return descricao;
	}
}
