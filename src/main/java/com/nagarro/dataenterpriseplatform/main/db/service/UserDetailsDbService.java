package com.nagarro.dataenterpriseplatform.main.db.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nagarro.dataenterpriseplatform.main.db.entity.UserDetailsEntity;
import com.nagarro.dataenterpriseplatform.main.db.repository.UserDetailsRepo;
import com.nagarro.dataenterpriseplatform.main.utils.Utility;

@Service
@Transactional
public class UserDetailsDbService {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserDetailsRepo repo;

	public UserDetailsEntity Register(UserDetailsEntity userDetail) {
		final UserDetailsEntity signupUser = new UserDetailsEntity();
		signupUser.setUsername(userDetail.getUsername());
		signupUser.setPassword(Utility.getEncodedPassword(passwordEncoder, userDetail.getPassword()));
		signupUser.setEmail(userDetail.getEmail());
		signupUser.setDomainType(userDetail.getDomainType());
		return repo.save(signupUser);
	}

	public List<UserDetailsEntity> getEmployees() {
		return repo.findAll();
	}

	public UserDetailsEntity fetchEmployee(String email) {
		final UserDetailsEntity emp = repo.findByEmail(email);

		return emp;
	}

	public boolean deleteEmployee(String id) {
		final UserDetailsEntity employee = repo.findById(id).get();
		if (employee != null) {
			repo.delete(employee);
			return true;
		} else {
			return false;
		}
	}

}
