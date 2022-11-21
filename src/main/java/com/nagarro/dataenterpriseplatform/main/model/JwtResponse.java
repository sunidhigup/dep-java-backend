package com.nagarro.dataenterpriseplatform.main.model;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {

	private String jwtToken;
	private String username;
	private String id;

	private List<GrantedAuthority> authorities;
	private String domainType;
}