package com.nagarro.dataenterpriseplatform.main.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepClientEntity;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowBuilderMetadataEntity;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowbuilderJobStatusEntity;
import com.nagarro.dataenterpriseplatform.main.db.service.DepClientDbService;
import com.nagarro.dataenterpriseplatform.main.db.service.DepFlowBuilderMetadataDbService;
import com.nagarro.dataenterpriseplatform.main.db.service.DepFlowbuilderJobStatusDbService;
import com.nagarro.dataenterpriseplatform.main.service.BatchService;

@Service
public class BatchServiceImpl implements BatchService{

	@Autowired
	private DepFlowBuilderMetadataDbService depFlowBuilderMetadataDbService;
	
	@Autowired
	private DepClientDbService depClientDbService;
	
	@Autowired
	private DepFlowbuilderJobStatusDbService depFlowbuilderJobStatusDbService;
	
	@Override
	public ResponseEntity<?> getAllBatch() {
		final List<DepFlowBuilderMetadataEntity> response = depFlowBuilderMetadataDbService.getAllBatches();
		if (response.size() <= 0) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		for(DepFlowBuilderMetadataEntity entity : response) {
			final DepClientEntity clientResponse = depClientDbService.getClientById(entity.getClient_id());
			entity.setClient_name(clientResponse.getClient_name());
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@Override
	public  DepFlowbuilderJobStatusEntity getJobStatus(DepFlowbuilderJobStatusEntity depFlowbuilderJobStatusEntity) {
		return depFlowbuilderJobStatusDbService.getStatus(depFlowbuilderJobStatusEntity,null);
	}

	@Override
	public DepFlowbuilderJobStatusEntity getJobStatus(String id) {
		return depFlowbuilderJobStatusDbService.getStatus(id);
	}

}
