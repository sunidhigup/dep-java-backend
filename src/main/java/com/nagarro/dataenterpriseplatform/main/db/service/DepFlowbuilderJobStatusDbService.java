package com.nagarro.dataenterpriseplatform.main.db.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowbuilderJobStatusEntity;
import com.nagarro.dataenterpriseplatform.main.db.repository.DepFlowbuilderJobStatusRepo;


@Service
@Transactional
public class DepFlowbuilderJobStatusDbService {

	@Autowired
	private DepFlowbuilderJobStatusRepo depFlowbuilderJobStatusRepo;

	public DepFlowbuilderJobStatusEntity getLogStatusById(String id) {
		return depFlowbuilderJobStatusRepo.findByExecution_id(id);
	}

	public boolean saveJobStatus(DepFlowbuilderJobStatusEntity statusTable) {
		depFlowbuilderJobStatusRepo.save(statusTable);
		return true;
	}

	public DepFlowbuilderJobStatusEntity getStatus(DepFlowbuilderJobStatusEntity data, String jobname) {
		return this.depFlowbuilderJobStatusRepo.findById(data.getExecution_id()).orElse(null) ;
		
	}
	
	public DepFlowbuilderJobStatusEntity getStatus(String id) {
		return  this.depFlowbuilderJobStatusRepo.findById(id).orElse(null) ;
	}
}
