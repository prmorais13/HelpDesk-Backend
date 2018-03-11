package com.prm.helpdesk.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.prm.helpdesk.entities.Ticket;

@Repository
public interface TicketRepository extends MongoRepository<Ticket, String>{
	
	Page<Ticket> findByUsuarioIdOrderByDataDesc(Pageable page, String usuarioId);
	
	Page<Ticket> findByTituloIgnoreCaseContainingAndStatusAndPrioridadeOrderByDataDesc(
			String titulo, String status, String prioridade, Pageable page);
	
	Page<Ticket> findByTituloIgnoreCaseContainingAndStatusAndPrioridadeAndUsuarioIdOrderByDataDesc(
			String titulo, String status, String prioridade, String usuarioId, Pageable page);
	
	Page<Ticket> findByTituloIgnoreCaseContainingAndStatusAndPrioridadeAndUsuarioDesignadoOrderByDataDesc(
			String titulo, String status, String prioridade, String usuarioDesignado, Pageable page);
	

	Page<Ticket> findByNumero(Integer numero, Pageable page);
}
