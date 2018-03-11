package com.prm.helpdesk.entities;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.prm.helpdesk.enums.Profile;

import lombok.Data;

@Document
@Data
public class User {
	
	@Id
	private String id;
	
	@Indexed(unique = true)
	@NotBlank(message = "Email deve ser informado.")
	@Email(message = "Email inválido.")
	private String email;
	
	@NotBlank(message = "Senha deve ser informada.")
	@Size(min = 6)
	private String senha;
	
	private Profile profiles;

}
