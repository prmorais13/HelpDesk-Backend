	package com.prm.helpdesk.enums;

public enum Status {
	
	NOVO ("Novo"),
	DESIGNADO ("Designado"),
	RESOLVIDO ("Resolvido"),
	APROVADO ("Aprovado"),
	REPROVADO ("Reprovado"),
	FECHADO ("Fechado");
	
	Status(String descricao) {
		this.descricao = descricao;
	}
	
	private String descricao;
	
	public String getDescricao() {
		return descricao;
	}
}
