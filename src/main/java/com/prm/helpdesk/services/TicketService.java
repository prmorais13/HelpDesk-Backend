package com.prm.helpdesk.services;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.prm.helpdesk.entities.ChangeStatus;
import com.prm.helpdesk.entities.Ticket;

@Component
public interface TicketService {
	
	Ticket createOrUpdate(Ticket ticket);
	Ticket findById(String id);
	void delete(String id);
	Page<Ticket> listTicket(int page, int count);
	Page<Ticket> findByCurrentUser(int page, int count, String userId);
	Page<Ticket> findByParameters(int page, int count, String titulo, String status, String prioridade);
	Page<Ticket> findByParametersAndCurrentUser(int page, int count, String titulo, String status, String prioridade, String userId);
	Page<Ticket> findByNumero(int page, int count, Integer numero);
	Page<Ticket> findByParametersAndUsuarioDesignado(int page, int count, String titulo, String status, String prioridade, String usuarioDesignado);
	Iterable<Ticket> findAll();
	
	Iterable<ChangeStatus> listChangeStatus(String ticketId);
	ChangeStatus createChangeStatus(ChangeStatus changeStatus);
}
