package com.nagarro.dataenterpriseplatform.main.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nagarro.dataenterpriseplatform.main.db.service.StatusBatchIdDbService;
import com.nagarro.dataenterpriseplatform.main.service.BatchIdService;

@Service
public class BatchIdServiceImpl implements BatchIdService  {
	
	@Autowired
	private StatusBatchIdDbService statusBatchIdDbService;
	
	public String fetchId(String batchId) {
        return statusBatchIdDbService.getBatchID(batchId);
        
	}
}
