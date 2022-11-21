package com.nagarro.dataenterpriseplatform.main.db.service;


import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.nagarro.dataenterpriseplatform.main.db.entity.DepRuleEngineJobStatusEntity;
import com.nagarro.dataenterpriseplatform.main.db.repository.DepRuleEngineJobStatusRepo;


@Service
@Transactional
public class DepRuleEngineJobStatusDbService {

	@Autowired
	private DepRuleEngineJobStatusRepo depRuleEngineJobStatusRepo;

	public DepRuleEngineJobStatusEntity getLogStatusById(String id) {
		return depRuleEngineJobStatusRepo.findById(id).get();
	}

	public DepRuleEngineJobStatusEntity getStatus(DepRuleEngineJobStatusEntity depRuleEngineJobStatusEntity) {
		return depRuleEngineJobStatusRepo.findById(depRuleEngineJobStatusEntity.getId()).get();

	}

	public DepRuleEngineJobStatusEntity getStatusTable(String id) {
		final String[] split_id = id.split("___");
		final DepRuleEngineJobStatusEntity list = depRuleEngineJobStatusRepo.findById(split_id[0]).get();
		return list;
	}
	
	public DepRuleEngineJobStatusEntity getStatus(String id) {
		return depRuleEngineJobStatusRepo.findById(id).orElse(null);

	}


}
