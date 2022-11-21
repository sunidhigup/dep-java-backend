package com.nagarro.dataenterpriseplatform.main.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nagarro.dataenterpriseplatform.main.db.entity.UserDetailsEntity;
import com.nagarro.dataenterpriseplatform.main.db.service.UserDetailsDbService;
import com.nagarro.dataenterpriseplatform.main.model.JwtRequest;
import com.nagarro.dataenterpriseplatform.main.model.JwtResponse;
import com.nagarro.dataenterpriseplatform.main.service.UserService;
import com.nagarro.dataenterpriseplatform.main.utils.JWTUtility;

import io.jsonwebtoken.ExpiredJwtException;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

	@Autowired
	private JWTUtility jwtUtility;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserService userService;

	@Autowired
	private UserDetailsDbService userDetailsDbService;

	@GetMapping("/")
	public String home() {
		return "Welcome to Data Enterprise Platform!!";
	}

	@PostMapping("/authenticate")
	public JwtResponse authenticate(@RequestBody JwtRequest jwtRequest, HttpServletResponse response) throws Exception {
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(), jwtRequest.getPassword()));

		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}

		try {
			final UserDetails userDetails = userService.loadUserByUsername(jwtRequest.getUsername());

			String newRoles = "";
			String newDomain = "";
			String newUserId = "";
			List<GrantedAuthority> roles = new ArrayList<>();
			for (GrantedAuthority role : userDetails.getAuthorities()) {
				if (role.getAuthority().startsWith("ROLE_")) {
					roles.add(role);
					newRoles = role.getAuthority();
				}

				else if (role.getAuthority().startsWith("USERID_")) {
					newUserId = role.getAuthority().substring(7);

				} else {
					newDomain = role.getAuthority();
				}
			}
			final String token = jwtUtility.generateToken(userDetails, newRoles);

			// Cookie cookie = new Cookie("jwtToken", token);
			// cookie.setDomain("localhost");
			// response.addCookie(cookie);

			// return new JwtResponse(token, userDetails.getUsername());

			return new JwtResponse(token, userDetails.getUsername(), newUserId, roles, newDomain);
		} catch (ExpiredJwtException e) {
			throw new Exception("JWT Token has been expired ");
		} catch (Exception e) {
			throw new Exception("User not found with username: " + jwtRequest.getUsername());
		}

	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody UserDetailsEntity userDetailsEntity) throws Exception {

		final UserDetailsEntity user = userDetailsDbService.Register(userDetailsEntity);
		if (user == null) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(userDetailsEntity);
	}
}