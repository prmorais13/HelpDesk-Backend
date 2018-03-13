package com.prm.helpdesk.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SumarioDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Integer novo;
	private Integer resolvido;
	private Integer aprovado;
	private Integer reprovado;
	private Integer designado;
	private Integer fechado;
	
}
