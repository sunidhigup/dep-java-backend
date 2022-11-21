package com.nagarro.dataenterpriseplatform.main.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepManagementDetailsEntity;
import com.nagarro.dataenterpriseplatform.main.db.service.DepManagementDetailsDbService;

@Service
public class UserService implements UserDetailsService {

	@Autowired
	private DepManagementDetailsDbService depManagementDetailsDbService;

	@SuppressWarnings("unchecked")
	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		final DepManagementDetailsEntity allUsers = depManagementDetailsDbService.GetUserDetaiInfo(userName);
		if (allUsers == null) {
			throw new UsernameNotFoundException("User not found with username: " + userName);
		}
		return new User(allUsers.getUsername(), allUsers.getPassword(), getAuthority(allUsers));
	}

	@SuppressWarnings("rawtypes")
	private Set getAuthority(DepManagementDetailsEntity user) {
		final Set<SimpleGrantedAuthority> authorities = new HashSet<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
		authorities.add(new SimpleGrantedAuthority("DOMAIN_" + user.getDomainType()));
		authorities.add(new SimpleGrantedAuthority("USERID_" + user.getId()));
		return authorities;
	}
}