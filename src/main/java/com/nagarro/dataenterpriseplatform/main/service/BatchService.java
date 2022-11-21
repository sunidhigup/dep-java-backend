package com.nagarro.dataenterpriseplatform.main.service;

import org.springframework.http.ResponseEntity;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowbuilderJobStatusEntity;

public interface BatchService {

	/**
	 * get all bathes with client name
	 * @return
	 */
	ResponseEntity<?> getAllBatch();
	
	/**
	 * 
	 * @param id
	 * @return
	 */
    DepFlowbuilderJobStatusEntity getJobStatus(DepFlowbuilderJobStatusEntity depFlowbuilderJobStatusEntity);
    
    /**
     * 
     * @param depFlowbuilderJobStatusEntity
     * @return
     */
    
    DepFlowbuilderJobStatusEntity getJobStatus(String depFlowbuilderJobStatusEntity);
}
