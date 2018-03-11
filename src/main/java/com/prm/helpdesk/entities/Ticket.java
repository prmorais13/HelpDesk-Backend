package com.prm.helpdesk.entities;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.prm.helpdesk.enums.Prioridade;
import com.prm.helpdesk.enums.Status;

import lombok.Data;

@Document
@Data
public class Ticket {
	
	@Id
	private String id;
	
	@DBRef(lazy = true)
	private User usuario;
	private Date data;
	private String titulo;
	private Integer numero;
	private Status status;
	private Prioridade prioridade;
	
	@DBRef(lazy = true)
	private User usuarioDesignado;
	private String descricao;
	private String imagem;
	
	@Transient
	private List<ChangeStatus> alteracoes;
}