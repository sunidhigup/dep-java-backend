package com.nagarro.dataenterpriseplatform.main.db.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nagarro.dataenterpriseplatform.main.db.entity.StatusBatchIdEntity;
import com.nagarro.dataenterpriseplatform.main.db.repository.StatusBatchIdRepo;

@Service
@Transactional
public class StatusBatchIdDbService {

	@Autowired
	private StatusBatchIdRepo statusBatchIdRepo;

	public void createId(StatusBatchIdEntity table) {
		try {
			StatusBatchIdEntity fetchedBatchId = this.statusBatchIdRepo.findByClient_job(table.getClient_job());

			if (fetchedBatchId != null) {
				this.statusBatchIdRepo.delete(fetchedBatchId);
			}

			this.statusBatchIdRepo.save(table);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getBatchID(String id) {
		StatusBatchIdEntity fetchedBatchId = this.statusBatchIdRepo.findByClient_job(id);
		if (fetchedBatchId != null)
			return fetchedBatchId.getBatch_id();

		return null;

	}

	public StatusBatchIdEntity getBatchIDDetails(String id) {
		StatusBatchIdEntity fetchedBatchId = this.statusBatchIdRepo.findByClient_job(id);
		return fetchedBatchId;
	}
}
