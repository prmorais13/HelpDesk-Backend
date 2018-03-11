package com.prm.helpdesk.entities;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.prm.helpdesk.enums.Status;

import lombok.Data;

@Document
@Data
public class ChangeStatus {
	
	private String id;
	private Ticket ticket;
	
	@DBRef(lazy = true)
	private User usuarioAlteracao;
	private Date dataAlteracao;
	private Status status;

}
