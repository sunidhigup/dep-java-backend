package com.nagarro.dataenterpriseplatform.main.db.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepMdmFlowBuilderFormEntity;
import com.nagarro.dataenterpriseplatform.main.db.repository.DepMdmFlowBuilderFormRepo;

@Service
public class DepMdmFlowBuilderFormService {

	@Autowired
	DepMdmFlowBuilderFormRepo depMdmFlowBuilderFormRepo;

	public void createNewMDMFormData(DepMdmFlowBuilderFormEntity entity) {

		List<DepMdmFlowBuilderFormEntity> OldEntity = this.depMdmFlowBuilderFormRepo
				.findByEntityName(entity.getEntityName());

		if (OldEntity.size() > 0)
			this.depMdmFlowBuilderFormRepo.delete(OldEntity.get(0));

		this.depMdmFlowBuilderFormRepo.save(entity);

	}

	public DepMdmFlowBuilderFormEntity getMDMFormByEntityName(String entityName) {

		List<DepMdmFlowBuilderFormEntity> list = this.depMdmFlowBuilderFormRepo.findByEntityName(entityName);

		if (list.size() == 0)
			return null;

		else
			return list.get(0);
	}

	public void deleteMDMFormByEntityName(String entityName) {

		DepMdmFlowBuilderFormEntity entity = this.getMDMFormByEntityName(entityName);

		this.depMdmFlowBuilderFormRepo.deleteById(entity.getFieldId());
	}
}
