package com.nagarro.dataenterpriseplatform.main.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepPreProcessorFileClassificationEntity;

public interface DepPreProcessorFileClassificationRepo extends JpaRepository<DepPreProcessorFileClassificationEntity,String> {

	@Query(value = "select * from dep_preprocessor_file_classification where client_name =?1 and batch_name=?2", nativeQuery = true)
	List<DepPreProcessorFileClassificationEntity> findByClient_nameAndBatch_name(String clientName,String BatchName);
}
