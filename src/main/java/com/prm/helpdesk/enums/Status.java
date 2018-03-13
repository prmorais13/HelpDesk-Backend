	package com.prm.helpdesk.enums;

public enum Status {
	
	NOVO ("Novo"),
	DESIGNADO ("Designado"),
	RESOLVIDO ("Resolvido"),
	APROVADO ("Aprovado"),
	REPROVADO ("Reprovado"),
	FECHADO ("Fechado");
	
	private String descricao;
	
	Status(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public static Status getStatus(String status) {
		
		switch (status) {
			case "NOVO": return NOVO;
	
			case "DESIGNADO": return DESIGNADO;
	
			case "RESOLVIDO": return RESOLVIDO;
			
			case "APROVADO": return APROVADO;
			
			case "REPROVADO": return REPROVADO;
			
			case "FECHADO": return REPROVADO;
			
			default: return NOVO;

		}
	}
}
