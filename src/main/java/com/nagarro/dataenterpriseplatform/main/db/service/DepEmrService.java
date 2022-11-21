package com.nagarro.dataenterpriseplatform.main.db.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepEmrEntity;
import com.nagarro.dataenterpriseplatform.main.db.repository.DepEmrRepo;

@Service
public class DepEmrService {

	@Autowired
	private DepEmrRepo depEmrRepo;

	public List<DepEmrEntity> fetchAllEmrList() {
		return this.depEmrRepo.findAll();
	}

}
