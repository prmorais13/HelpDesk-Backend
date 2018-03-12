package com.prm.helpdesk.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
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

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;

import com.prm.helpdesk.entities.User;
import com.prm.helpdesk.response.Response;
import com.prm.helpdesk.services.UserService;

@RestController
@RequestMapping("api/users")
@CrossOrigin(origins = "*")
public class UserController {
	 
	@Autowired
	private UserService userService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Response<User>> create(HttpServletRequest req, @RequestBody User user,
			BindingResult result) {
		
		Response<User> response = new Response<User>();
		
		try {
			this.validarCriacaoUsuario(user, result);
			if(result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}
			user.setSenha(passwordEncoder.encode(user.getSenha()));
			User UsuarioPersistido = this.userService.createOrUpdate(user);
			response.setData(UsuarioPersistido);
			
		} catch (DuplicateKeyException dKE) {
			response.getErrors().add("Email já cadastrado.");
			return ResponseEntity.badRequest().body(response);
		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
		
		 return ResponseEntity.ok(response);
	}
	
	@PutMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Response<User>> update (HttpServletRequest req, @RequestBody User user,
			BindingResult result) {
		
		Response<User> response = new Response<>();
		
		try {
			this.validarAlteracaoUsuario(user, result);
			if(result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}
			user.setSenha(passwordEncoder.encode(user.getSenha()));
			User UsuarioPersistido = this.userService.createOrUpdate(user);
			response.setData(UsuarioPersistido);
	
		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Response<User>> findById(@PathVariable String id) {
		
		Response<User> response = new Response<User>();
		
		User usuarioEncontrado = this.userService.findById(id);
		
		if (usuarioEncontrado == null) {
			response.getErrors().add("Usuário não encontrado com ID: " + id );
			return ResponseEntity.badRequest().body(response);
		}
		response.setData(usuarioEncontrado);
		return ResponseEntity.ok(response);
	}
	
	@DeleteMapping("{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Response<String>> delete(@PathVariable String id) {
		
		Response<String> response = new Response<>();
		
		User usuarioEncontrado = this.userService.findById(id);
		
		if (usuarioEncontrado == null) {
			response.getErrors().add("Usuário não encontrado com ID: " + id );
			return ResponseEntity.badRequest().body(response);
		}
		
		this.userService.delete(id);
		return ResponseEntity.ok(new Response<String>());
	}
	
	@GetMapping("{page}/{count}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Response<Page<User>>> findAll(@PathVariable int page, @PathVariable int count) {
		Response<Page<User>> response = new Response<>();
		
		Page<User> usuarios = this.userService.findAll(page, count);
		response.setData(usuarios);
		
		return ResponseEntity.ok(response);
	}
	
	//Método auxiliar create
	private void validarCriacaoUsuario(User user, BindingResult result) {
			
		if (user.getEmail() == null) {
			result.addError(new ObjectError("User", "Email não informado."));
		}
	}
	
	//Método auxiliar update
	private void validarAlteracaoUsuario(User user, BindingResult result) {
		
		if (user.getId() == null) {
			result.addError(new ObjectError("User", "ID não informado."));
		}
		
		if (user.getEmail() == null) {
			result.addError(new ObjectError("User", "Email não informado."));
		}
	}

}
