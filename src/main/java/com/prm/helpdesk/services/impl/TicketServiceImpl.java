package com.prm.helpdesk.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.prm.helpdesk.entities.ChangeStatus;
import com.prm.helpdesk.entities.Ticket;
import com.prm.helpdesk.repositories.ChangeStatusRepository;
import com.prm.helpdesk.repositories.TicketRepository;
import com.prm.helpdesk.services.TicketService;

@Service
public class TicketServiceImpl implements TicketService {
	
	@Autowired
	private TicketRepository ticketRepository;
	
	@Autowired
	private ChangeStatusRepository changeStatusRepository;

	@Override
	public Ticket createOrUpdate(Ticket ticket) {
		return this.ticketRepository.save(ticket);
	}

	@Override
	public Ticket findById(String id) {
		return this.ticketRepository.findOne(id);
	}

	@Override
	public void delete(String id) {
		this.ticketRepository.delete(id);
	}

	@Override
	public Page<Ticket> listTicket(int page, int count) {
		Pageable pages = new PageRequest(page, count);
		return this.ticketRepository.findAll(pages);
	}

	@Override
	public Page<Ticket> findByCurrentUser(int page, int count, String usuarioId) {
		Pageable pages = new PageRequest(page, count);
		return this.ticketRepository.findByUsuarioIdOrderByDataDesc(pages, usuarioId);
	}

	@Override
	public Page<Ticket> findByParameters(int page, int count, String titulo, String status, String prioridade) {
		Pageable pages = new PageRequest(page, count);
		return this.ticketRepository.findByTituloIgnoreCaseContainingAndStatusAndPrioridadeOrderByDataDesc(
				titulo, status, prioridade, pages);
	}

	@Override
	public Page<Ticket> findByParametersAndCurrentUser(int page, int count, String titulo, String status,
			String prioridade, String usuarioId) {
		Pageable pages = new PageRequest(page, count);
		return this.ticketRepository.findByTituloIgnoreCaseContainingAndStatusAndPrioridadeAndUsuarioIdOrderByDataDesc(
				titulo, status, prioridade, usuarioId, pages);
	}

	@Override
	public Page<Ticket> findByNumero(int page, int count, Integer numero) {
		Pageable pages = new PageRequest(page, count);
		return this.ticketRepository.findByNumero(numero, pages);
	}

	@Override
	public Page<Ticket> findByParametersAndUsuarioDesignado(int page, int count, String titulo, String status,
			String prioridade, String usuarioDesignado) {
		Pageable pages = new PageRequest(page, count);
		return this.ticketRepository.findByTituloIgnoreCaseContainingAndStatusAndPrioridadeAndUsuarioDesignadoOrderByDataDesc(
				titulo, status, prioridade, usuarioDesignado, pages);
	}

	@Override
	public Iterable<Ticket> findAll() {
		return this.ticketRepository.findAll();
	}

	@Override
	public Iterable<ChangeStatus> listChangeStatus(String ticketId) {
		return this.changeStatusRepository.findByTicketIdOrderByDataAlteracaoDesc(ticketId);
	}

	@Override
	public ChangeStatus createChangeStatus(ChangeStatus changeStatus) {
		return this.changeStatusRepository.save(changeStatus);
	}
	
	
}
