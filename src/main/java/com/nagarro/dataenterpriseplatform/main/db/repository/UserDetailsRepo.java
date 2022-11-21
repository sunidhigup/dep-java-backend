package com.nagarro.dataenterpriseplatform.main.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepTableRulesEntity;
import com.nagarro.dataenterpriseplatform.main.db.entity.UserDetailsEntity;


@Repository
public interface  UserDetailsRepo extends JpaRepository<UserDetailsEntity, String> {
	
	UserDetailsEntity findByEmail(String email);
	

}
