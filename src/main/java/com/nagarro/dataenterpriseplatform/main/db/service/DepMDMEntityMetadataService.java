package com.nagarro.dataenterpriseplatform.main.db.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepMDMEntityMetadataEntity;
import com.nagarro.dataenterpriseplatform.main.db.repository.DepMDMEntityMetadataRepo;

@Service
public class DepMDMEntityMetadataService {

	@Autowired
	DepMDMEntityMetadataRepo depMDMEntityMetadataRepo;

	public boolean createEntity(DepMDMEntityMetadataEntity entity) {

		List<DepMDMEntityMetadataEntity> latestReplies = this.depMDMEntityMetadataRepo
				.findByEntityName(entity.getEntityName());

		if (latestReplies.size() == 1 && entity.getAttribute() != null) {
			this.depMDMEntityMetadataRepo.save(entity);
			return false;
		}
		if (latestReplies.size() == 0) {
			this.depMDMEntityMetadataRepo.save(entity);
			return false;
		}

		return true;

	}

	public List<DepMDMEntityMetadataEntity> getAllEntity() {
		List<DepMDMEntityMetadataEntity> list = new ArrayList<>();

		try {
			list = this.depMDMEntityMetadataRepo.findAll();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return list;
	}

	public DepMDMEntityMetadataEntity getEntityById(String entityId) {
		return this.depMDMEntityMetadataRepo.findById(entityId).orElse(null);
	}

	public boolean deleteEntityById(String entityId) {

		try {
			DepMDMEntityMetadataEntity entity = getEntityById(entityId);
			if (entity != null)
				this.depMDMEntityMetadataRepo.delete(entity);

			else
				return false;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

}
