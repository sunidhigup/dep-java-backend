package com.nagarro.dataenterpriseplatform.main.db.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowbuilderJobStatusEntity;

@Repository
public interface DepFlowbuilderJobStatusRepo extends JpaRepository<DepFlowbuilderJobStatusEntity, String>{


	@Query(value = "Select * from dep_flowbuilder_job_status u where u.execution_id = ?1", nativeQuery = true)
	public DepFlowbuilderJobStatusEntity findByExecution_id(String id);
	
}
