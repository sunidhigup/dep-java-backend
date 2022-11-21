package com.nagarro.dataenterpriseplatform.main.db.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepClientEntity;
import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowBuilderFormEntity;
import com.nagarro.dataenterpriseplatform.main.db.repository.DepFlowBuilderFormRepo;

@Service
@Transactional
public class DepFlowBuilderFormDbService {

	@Value("${aws.bucketName}")
	private String awsBucketName;

	@Value("${aws.flowBuilderJsonLocation}")
	private String awsFlowBuilderJsonLocation;

	@Autowired
	private DepClientDbService depClientDbService;

	@Autowired
	private AmazonS3 amazonS3;

	@Autowired
	private DepFlowBuilderFormRepo depFlowBuilderFormRepo;

	public void createNewFormData(DepFlowBuilderFormEntity form) {
		try {
			DepFlowBuilderFormEntity fetchedFormData = this.depFlowBuilderFormRepo
					.findByClient_idAndBatch_idAndJobname(form.getClient_id(), form.getBatch_id(), form.getJobName());

			if (fetchedFormData != null) {
				this.depFlowBuilderFormRepo.delete(fetchedFormData);
			}

			this.depFlowBuilderFormRepo.save(form);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DepFlowBuilderFormEntity getFormData(String client_id, String batch_id, String job) {

		DepFlowBuilderFormEntity fetchedFormData = this.depFlowBuilderFormRepo
				.findByClient_idAndBatch_idAndJobname(client_id, batch_id, job);
		return fetchedFormData;
	}

	public void deleteFormData(String client_id, String batch_id, String job) {
		DepFlowBuilderFormEntity fetchedFormData = this.depFlowBuilderFormRepo
				.findByClient_idAndBatch_idAndJobname(client_id, batch_id, job);

		if (fetchedFormData != null) {
			this.depFlowBuilderFormRepo.delete(fetchedFormData);
		}
	}

	public DepClientEntity getClientByClientNameAndUserId(String clientName, String userId) {
		return this.depClientDbService.getClientByNameAndUserId(clientName, userId);
	}

}
