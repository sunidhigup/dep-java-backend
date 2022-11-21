package com.nagarro.dataenterpriseplatform.main.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nagarro.dataenterpriseplatform.main.db.entity.DepFlowBuilderJobNameEntity;
import org.springframework.data.jpa.repository.Query;

public interface DepFlowBuilderJobNameRepo extends JpaRepository<DepFlowBuilderJobNameEntity, String> {
	@Query(value = "delete from dep_flowbuilder_job_name u where u.batch_id = ?1", nativeQuery = true)
	public void deleteAllByBatch_id(String batch_id);

	@Query(value = "Select * from dep_flowbuilder_job_name u where u.client_id = ?1 and u.batch_id = ?2 and u.input_ref_key = ?3", nativeQuery = true)
	public DepFlowBuilderJobNameEntity findByClient_idAndBatch_idAndInput_ref_key(String client_id, String batch_id,
			String input_ref_key);

	@Query(value = "Select * from dep_flowbuilder_job_name u where u.client_id = ?1 and u.batch_id = ?2", nativeQuery = true)
	public List<DepFlowBuilderJobNameEntity> findByClient_idAndBatch_id(String client_id, String batch_id);

	@Query(value = "Select * from dep_flowbuilder_job_name u where u.client_id = ?1", nativeQuery = true)
	public List<DepFlowBuilderJobNameEntity> findByClient_id(String client_id);

	@Query(value = "Select * from dep_flowbuilder_job_name u where u.client_id = ?1 and u.batch_id = ?2 and u.batch_name = ?3 and u.input_ref_key = ?4", nativeQuery = true)
	public List<DepFlowBuilderJobNameEntity> findByClient_idAndBatch_idAndBatch_nameAndInput_ref_key(String client_id,
			String batch_id, String batchname, String input_ref_key);

}
