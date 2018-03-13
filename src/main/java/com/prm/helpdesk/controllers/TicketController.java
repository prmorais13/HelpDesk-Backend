package com.prm.helpdesk.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prm.helpdesk.dto.SumarioDTO;
import com.prm.helpdesk.entities.ChangeStatus;
import com.prm.helpdesk.entities.Ticket;
import com.prm.helpdesk.entities.User;
import com.prm.helpdesk.enums.Profile;
import com.prm.helpdesk.enums.Status;
import com.prm.helpdesk.response.Response;
import com.prm.helpdesk.security.jwt.JwtTokenUtil;
import com.prm.helpdesk.services.TicketService;
import com.prm.helpdesk.services.UserService;

@RestController
@RequestMapping("api/tickets")
@CrossOrigin(origins = "*")
public class TicketController {
	 
	@Autowired
	private TicketService ticketService;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private UserService userService;
	
	@PostMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
	public ResponseEntity<Response<Ticket>> create(HttpServletRequest req, @RequestBody Ticket ticket,
			BindingResult result) {
		
		Response<Ticket> response = new Response<>();
		
		try {
			this.validarCriacaoTicket(ticket, result);
			if(result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}
			
			ticket.setStatus(Status.getStatus("NOVO"));
			ticket.setUsuario(usuarioFromRequest(req));
			ticket.setData(new Date());
			ticket.setNumero(gerarNumero());
			Ticket ticketPersistido = this.ticketService.createOrUpdate(ticket);
			response.setData(ticketPersistido);
			
		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
		
		 return ResponseEntity.ok(response);
	}
	
	//Método auxliar
	private Integer gerarNumero() {
		Random random = new Random();
		
		return random.nextInt(9999);
	}

	//Método auxliar
	private User usuarioFromRequest(HttpServletRequest req) {
		String token = req.getHeader("Authorization");
		String email = this.jwtTokenUtil.getUsernameFromToken(token);

		return this.userService.findByEmail(email);
	}

	@PutMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
	public ResponseEntity<Response<Ticket>> update (HttpServletRequest req, @RequestBody Ticket ticket,
			BindingResult result) {
		
		Response<Ticket> response = new Response<>();
		
		try {
			this.validarAlteracaoTicket(ticket, result);
			
			if(result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}
			
			Ticket ticketAtual = this.ticketService.findById(ticket.getId());
			
			if (ticketAtual == null) {
				response.getErrors().add("Ticket não encontrado com ID: " + ticket.getId() );
				return ResponseEntity.badRequest().body(response);
			}
			
			ticket.setStatus(ticketAtual.getStatus());
			ticket.setUsuario(ticketAtual.getUsuario());
			ticket.setData(ticketAtual.getData());
			ticket.setNumero(ticketAtual.getNumero());
			
			if (ticketAtual.getUsuarioDesignado() != null) {
				ticket.setUsuarioDesignado(ticketAtual.getUsuarioDesignado());
			}
			
			Ticket ticketPersistido = this.ticketService.createOrUpdate(ticket);
			response.setData(ticketPersistido);
	
		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE', 'TECNICO')")
	public ResponseEntity<Response<Ticket>> findById(@PathVariable String id) {
		
		Response<Ticket> response = new Response<>();
		
		Ticket ticketPesquisado = this.ticketService.findById(id);
		
		if (ticketPesquisado == null) {
			response.getErrors().add("Ticket não encontrado com ID: " + id );
			return ResponseEntity.badRequest().body(response);
		}
		
		List<ChangeStatus> alteracaoStatus = new ArrayList<>();
		Iterable<ChangeStatus> alteracaoStatusAtuais = this.ticketService.listChangeStatus(ticketPesquisado.getId());
		
		for (Iterator<ChangeStatus> iterator = alteracaoStatusAtuais.iterator(); iterator.hasNext();) {
			ChangeStatus changeStatus = iterator.next();
			changeStatus.setTicket(null);
			alteracaoStatus.add(changeStatus);
		}
		
		ticketPesquisado.setAlteracoes(alteracaoStatus);
		response.setData(ticketPesquisado);
		return ResponseEntity.ok(response);
	}
	
	@DeleteMapping("{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE', 'TECNICO')")
	public ResponseEntity<Response<String>> delete(@PathVariable String id) {
		
		Response<String> response = new Response<>();
		
		Ticket ticketPesquisado = this.ticketService.findById(id);
		
		if (ticketPesquisado == null) {
			response.getErrors().add("Ticket não encontrado com ID: " + id );
			return ResponseEntity.badRequest().body(response);
		}
		
		this.ticketService.delete(id);
		return ResponseEntity.ok(new Response<String>());
	}
	
	@GetMapping("{page}/{count}")
	@PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE', 'TECNICO')")
	public ResponseEntity<Response<Page<Ticket>>> findAll(HttpServletRequest req,
			@PathVariable int page, @PathVariable int count) {
		
		Response<Page<Ticket>> response = new Response<>();
		
		Page<Ticket> tickets = null;
		
		User usuarioLogado = this.usuarioFromRequest(req);
		
		if (usuarioLogado.getProfiles().equals(Profile.ROLE_TECNICO)) {
			tickets = this.ticketService.listTicket(page, count);
		} else if (usuarioLogado.getProfiles().equals(Profile.ROLE_CLIENTE)) {
			tickets = this.ticketService.findByCurrentUser(page, count, usuarioLogado.getId());
		} else {
			tickets = this.ticketService.findByCurrentUser(page, count, usuarioLogado.getId());
		}
		response.setData(tickets);
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("{page}/{count}/{numero}/{titulo}/{status}/{prioridade}/{atribuido}")
	@PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE', 'TECNICO')")
	public ResponseEntity<Response<Page<Ticket>>> findByParams(
			HttpServletRequest req, @PathVariable int page,
			@PathVariable int count, @PathVariable  Integer numero,
			@PathVariable String titulo, @PathVariable String status, 
			@PathVariable String prioridade, @PathVariable boolean atribuido) {
		
		titulo = titulo.equals("uninformed") ? "" : titulo;
		status = status.equals("uninformed") ? "" : status;
		prioridade = prioridade.equals("uninformed") ? "" : prioridade;
			
		Response<Page<Ticket>> response = new Response<>();
		
		Page<Ticket> tickets = null;
		
		if (numero > 0) {
			tickets = this.ticketService.findByNumero(page, count, numero);
		
		} else {
			User usuarioLogado = this.usuarioFromRequest(req);
			
			if (usuarioLogado.getProfiles().equals(Profile.ROLE_TECNICO)) {
				if (atribuido) {
					tickets = this.ticketService.findByParametersAndUsuarioDesignado(
							page, count, titulo, status, prioridade, usuarioLogado.getId());
				} else {
					tickets = this.ticketService.findByParameters(
							page, count, titulo, status, prioridade);
				}
			
			} else {
				tickets = this.ticketService.findByCurrentUser(page, count, usuarioLogado.getId());
			}
		}
		
		response.setData(tickets);
		
		return ResponseEntity.ok(response);
		
		
	}
	
	@PutMapping("{id}/{status}")
	@PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE', 'TECNICO')")
	public ResponseEntity<Response<Ticket>> mudarStatus(
			@PathVariable String id, @PathVariable String status,
			HttpServletRequest req, @RequestBody Ticket ticket,
			BindingResult result) {
		
		Response<Ticket> response = new Response<>();
		
		try {
			this.validarAlteracaoStatus(id, status, result);
			
			if(result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}
			
			Ticket ticketaAtual = this.ticketService.findById(id);
			ticketaAtual.setStatus(Status.getStatus(status));
			
			if (status.equals("DESIGNADO")) {
				ticketaAtual.setUsuarioDesignado(this.usuarioFromRequest(req));
			}
			
			Ticket ticketPersistido = this.ticketService.createOrUpdate(ticketaAtual);
			
			ChangeStatus statusAlterado = new ChangeStatus();
			statusAlterado.setUsuarioAlteracao(this.usuarioFromRequest(req));
			statusAlterado.setDataAlteracao(new Date());
			statusAlterado.setStatus(Status.getStatus(status));
			statusAlterado.setTicket(ticketPersistido);
			
			this.ticketService.createChangeStatus(statusAlterado);
			
			response.setData(ticketPersistido);
			
		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/sumario")
	@PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE', 'TECNICO')")
	public ResponseEntity<Response<SumarioDTO>> findSumary() {
		
		Response<SumarioDTO> response = new Response<>();
		
		SumarioDTO sumarioDTO = new SumarioDTO();
		
		int novo = 0;
		int resolvido = 0;
		int aprovado = 0;
		int reprovado = 0;
		int designado = 0;
		int fechado = 0;
		
		Iterable<Ticket> tickets = this.ticketService.findAll();
		
		if (tickets != null) {
			for (Iterator<Ticket> iterator = tickets.iterator(); iterator.hasNext();) {
				Ticket ticket = (Ticket) iterator.next();
				
				if (ticket.getStatus().equals(Status.NOVO)) {
					novo++;
				}
				
				if (ticket.getStatus().equals(Status.RESOLVIDO)) {
					resolvido++;
				}
				
				if (ticket.getStatus().equals(Status.APROVADO)) {
					aprovado++;
				}
				
				if (ticket.getStatus().equals(Status.REPROVADO)) {
					reprovado++;
				}
				
				if (ticket.getStatus().equals(Status.DESIGNADO)) {
					designado++;
				}
				
				if (ticket.getStatus().equals(Status.FECHADO)) {
					fechado++;
				}
			}
		}
		
		sumarioDTO.setNovo(novo);
		sumarioDTO.setResolvido(resolvido);
		sumarioDTO.setAprovado(aprovado);
		sumarioDTO.setReprovado(reprovado);
		sumarioDTO.setDesignado(designado);
		sumarioDTO.setFechado(fechado);
		
		response.setData(sumarioDTO);
		
		return ResponseEntity.ok(response);
	}
	
	//Método auxiliar create
	private void validarAlteracaoStatus(String id, String status, BindingResult result) {
			
		if (id == null || id.equals("")) {
			result.addError(new ObjectError("Ticket", "ID não informado."));
		}
		
		if (status == null || status.equals("")) {
			result.addError(new ObjectError("Ticket", "Status não informado."));
		}
	}
	
	//Método auxiliar create
	private void validarCriacaoTicket(Ticket ticket, BindingResult result) {
		
		if (ticket.getTitulo() == null) {
			result.addError(new ObjectError("Ticket", "Título não informado."));
		}
	}
	
	//Método auxiliar update
	private void validarAlteracaoTicket(Ticket ticket, BindingResult result) {
		
		if (ticket.getId() == null) {
			result.addError(new ObjectError("Ticket", "ID não informado."));
		}
		
		if (ticket.getTitulo() == null) {
			result.addError(new ObjectError("Ticket", "Título não informado."));
		}
	}

}
