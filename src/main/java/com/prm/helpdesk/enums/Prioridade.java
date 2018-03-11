	package com.prm.helpdesk.enums;

public enum Prioridade {
	
	ALTA ("Alta"),
	NORMAL ("Normal"),
	BAIXA ("Baixa");
	
	Prioridade(String descricao) {
		this.descricao = descricao;
	}
	
	private String descricao;
	
	public String getDescricao() {
		return descricao;
	}
}
