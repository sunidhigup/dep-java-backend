package com.nagarro.dataenterpriseplatform.main.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepRuleEngineMetaDataEntity;

@Repository
public interface DepRuleEngineMetaDataRepo extends JpaRepository<DepRuleEngineMetaDataEntity, String>{
	

}
