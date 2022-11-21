package com.nagarro.dataenterpriseplatform.main.db.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepRuleEngineMetaDataEntity;
import com.nagarro.dataenterpriseplatform.main.db.repository.DepRuleEngineMetaDataRepo;

@Service
@Transactional
public class DepRuleEngineMetaDataDbService {

	@Autowired
	private DepRuleEngineMetaDataRepo depRuleEngineMetadataRepo;

	public void addTableRuleMetadata(DepRuleEngineMetaDataEntity ruleData) {
		depRuleEngineMetadataRepo.save(ruleData);

	}

}
